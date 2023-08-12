package com.benefitj.jpuppeteer.chromium;

import com.benefitj.jpuppeteer.Event;
import lombok.Data;

import java.util.List;

/**
 * This domain allows interacting with the FedCM dialog. EXPERIMENTAL
 */
@ChromiumApi("FedCm")
public interface FedCm {

  /**
   *
   */
  void disable();

  /**
   * @param dialogId        string
   * @param triggerCooldown boolean
   */
  void dismissDialog(String dialogId, Boolean triggerCooldown);

  /**
   * @param disableRejectionDelay Allows callers to disable the promise rejection delay that would normally happen,
   *                              if this is unimportant to what's being tested.
   *                              (step 4 of https://fedidcg.github.io/FedCM/#browser-api-rp-sign-in)
   */
  void enable(Boolean disableRejectionDelay);

  /**
   * Resets the cooldown time, if any, to allow the next FedCM call to show a dialog even if one was recently dismissed by the user.
   */
  void resetCooldown();

  /**
   * @param dialogId     string
   * @param accountIndex integer
   */
  void selectAccount(String dialogId, Integer accountIndex);

  /**
   * 事件
   */
  @Event("FedCm")
  public interface Events {

    /**
     * @param dialogId   string
     * @param dialogType DialogType
     * @param accounts   array[ Account ]
     * @param title      string
     *                   These exist primarily so that the caller can verify the RP context was used appropriately.
     * @param subtitle   string
     */
    @Event("dialogShown")
    void dialogShown(String dialogId, DialogType dialogType, List<Account> accounts, String title, String subtitle);

  }

  /**
   * Corresponds to IdentityRequestAccount
   */
  @Data
  public class Account {
    String accountId;
    String email;
    String name;
    String givenName;
    String pictureUrl;
    String idpConfigUrl;
    String idpSigninUrl;
    LoginState loginState;
    /**
     * These two are only set if the loginState is signUp
     */
    String termsOfServiceUrl;
    String privacyPolicyUrl;
  }

  /**
   * Whether the dialog shown is an account chooser or an auto re-authentication dialog.
   * Allowed Values: AccountChooser, AutoReauthn, ConfirmIdpSignin
   */
  public enum DialogType {
    AccountChooser, AutoReauthn, ConfirmIdpSignin
  }

  /**
   * Whether this is a sign-up or sign-in action for this account, i.e. whether this account has ever been used to sign in
   * to this RP before.
   * Allowed Values: SignIn, SignUp
   */
  public enum LoginState {
    SignIn, SignUp
  }

}
