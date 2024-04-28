package com.benefitj.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class DefaultMethods {
  private static final MethodHandleLookup methodHandleLookup = MethodHandleLookup.getMethodHandleLookup();

  public DefaultMethods() {
  }

  public static MethodHandle lookupMethodHandle(Method method) throws ReflectiveOperationException {
    return methodHandleLookup.lookup(method);
  }

  static enum MethodHandleLookup {
    OPEN {
      private final Optional<Constructor<MethodHandles.Lookup>> constructor = MethodHandleLookup.getLookupConstructor();

      MethodHandle lookup(Method method) throws ReflectiveOperationException {
        Constructor<MethodHandles.Lookup> constructor = (Constructor)this.constructor.orElseThrow(() -> {
          return new IllegalStateException("Could not obtain MethodHandles.lookup constructor");
        });
        return ((MethodHandles.Lookup)constructor.newInstance(method.getDeclaringClass())).unreflectSpecial(method, method.getDeclaringClass());
      }

      boolean isAvailable() {
        return this.constructor.isPresent();
      }
    },
    ENCAPSULATED {
      Method privateLookupIn = this.findBridgeMethod();

      MethodHandle lookup(Method method) throws ReflectiveOperationException {
        MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        return this.getLookup(method.getDeclaringClass()).findSpecial(method.getDeclaringClass(), method.getName(), methodType, method.getDeclaringClass());
      }

      private Method findBridgeMethod() {
        try {
          return MethodHandles.class.getDeclaredMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (ReflectiveOperationException var2) {
          return null;
        }
      }

      private MethodHandles.Lookup getLookup(Class<?> declaringClass) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        if (this.privateLookupIn != null) {
          try {
            return (MethodHandles.Lookup)this.privateLookupIn.invoke((Object)null, declaringClass, lookup);
          } catch (ReflectiveOperationException var4) {
            return lookup;
          }
        } else {
          return lookup;
        }
      }

      boolean isAvailable() {
        return true;
      }
    };

    private MethodHandleLookup() {
    }

    abstract MethodHandle lookup(Method var1) throws ReflectiveOperationException;

    abstract boolean isAvailable();

    public static MethodHandleLookup getMethodHandleLookup() {
      return (MethodHandleLookup) Arrays.stream(values()).filter(MethodHandleLookup::isAvailable).findFirst().orElseThrow(() -> {
        return new IllegalStateException("No MethodHandleLookup available!");
      });
    }

    private static Optional<Constructor<MethodHandles.Lookup>> getLookupConstructor() {
      try {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
        if (!constructor.isAccessible()) {
          constructor.setAccessible(true);
        }

        return Optional.of(constructor);
      } catch (Exception var1) {
        Exception ex = var1;
        if (ex.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
          return Optional.empty();
        } else {
          throw new IllegalStateException(ex);
        }
      }
    }
  }
}
