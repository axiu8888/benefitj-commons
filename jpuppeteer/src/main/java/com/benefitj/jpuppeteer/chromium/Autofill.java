package com.benefitj.jpuppeteer.chromium;


import lombok.Data;

import java.util.List;

/**
 *
 */
@ChromiumApi("Autofill")
public interface Autofill {

  /**
   * Set addresses so that developers can verify their forms implementation.
   *
   * @param addresses array[ Address ]
   */
  void setAddresses(List<Address> addresses);

  /**
   * Trigger autofill on a form identified by the fieldId. If the field and related form cannot be autofilled, returns an error.
   *
   * @param fieldId DOM.BackendNodeId
   *                Identifies a field that serves as an anchor for autofill.
   * @param frameId Page.FrameId
   *                Identifies the frame that field belongs to.
   * @param card    CreditCard
   *                Credit card information to fill out the form. Credit card data is not saved.
   */
  void trigger(String frameId, String fieldId, CreditCard card);

//  @Event("Autofill")
//  public interface Events {
//  }

  @Data
  public class Address {
    /**
     * array[ AddressField ]
     * fields and values defining a test address.
     */
    List<AddressField> fields;
  }

  /**
   *
   */
  @Data
  public class AddressField {
    /**
     * address field name, for example GIVEN_NAME.
     */
    String name;
    /**
     * address field name, for example Jon Doe.
     */
    String value;
  }

  @Data
  public class CreditCard {
    /**
     * 16-digit credit card number.
     */
    String number;
    /**
     * Name of the credit card owner.
     */
    String name;
    /**
     * 2-digit expiry month.
     */
    String expiryMonth;

    /**
     * 4-digit expiry year.
     */
    String expiryYear;

    /**
     * 3-digit card verification code.
     */
    String cvc;

  }

}
