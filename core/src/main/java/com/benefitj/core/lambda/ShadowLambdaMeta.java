package com.benefitj.core.lambda;

import java.lang.invoke.SerializedLambda;

public class ShadowLambdaMeta implements LambdaMeta {
  private final SerializedLambda lambda;

  public ShadowLambdaMeta(SerializedLambda lambda) {
    this.lambda = lambda;
  }

  @Override
  public String getImplMethodName() {
    return lambda.getImplMethodName();
  }

  @Override
  public Class<?> getInstantiatedClass() {
    String instantiatedMethodType = lambda.getInstantiatedMethodType();
    String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(";")).replace("\\", ".");
    try {
      return Class.forName(instantiatedType);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

}

