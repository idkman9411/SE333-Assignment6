package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Paths;


public class PlaywrightTraditionalTest {
    @Test
    public void testStore() {
        System.out.println("Start");
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1280, 720)
                    .setRecordVideoDir(Paths.get(System.getProperty("user.dir"), "testVideos"))
                    .setRecordVideoSize(1280, 720));
            Page page = context.newPage();
            System.out.println(page.video().path());
            page.navigate("https://depaul.bncollege.com/");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("earbuds");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).press("Enter");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
            page.getByRole(AriaRole.LISTITEM).filter(new Locator.FilterOptions().setHasText("brand JBL (12)")).getByRole(AriaRole.IMG).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black (9)")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Price Over $50 (8)")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
            assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING).first()).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
            assertThat(page.getByLabel("main")).containsText("sku 668972707");
            assertThat(page.getByLabel("main")).containsText("$164.98");
            assertThat(page.getByLabel("main")).containsText("Adaptive noise cancelling allows awareness of environment when gaming on the go. Light weight, durable, water resist. USB-C dongle for low latency connection < than 30ms.");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).click();
            assertThat(page.locator("#headerDesktopView")).containsText("Cart 1 items");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
            assertThat(page.getByLabel("main")).containsText("Your Shopping Cart");
            assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
            assertThat(page.getByLabel("main")).containsText("(1 Item)");
            assertThat(page.getByLabel("main")).containsText("$164.98");
            page.getByText("FAST In-Store PickupDePaul").click();
            assertThat(page.getByLabel("main")).containsText("Subtotal $164.98");
            assertThat(page.getByLabel("main")).containsText("Handling To support the bookstore's ability to provide a best-in-class online and campus bookstore experience, and to offset the rising costs of goods and services, an online handling fee of $3.00 per transaction is charged. This fee offsets additional expenses including fulfillment, distribution, operational optimization, and personalized service. No minimum purchase required. $3.00");
            assertThat(page.getByLabel("main")).containsText("Taxes TBD");
            assertThat(page.getByLabel("main")).containsText("Estimated Total $167.98");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).fill("TEST");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Promo Code")).click();
            assertThat(page.locator("#js-voucher-result")).containsText("The coupon code entered is not valid.");
            //Locator bttn = page.getByText("Proceed To Checkout").first();
            //bttn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(60000));
            //bttn.scrollIntoViewIfNeeded();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).first().click();
            //page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).nth(1).click();
            //
            assertThat(page.getByLabel("main")).containsText("Create Account");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
            assertThat(page.getByLabel("main")).containsText("Contact Information");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).fill("Jane");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).fill("Doe");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).fill("dpalumni@depaul.edu");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).fill("3123628000");
            assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
            assertThat(page.getByLabel("main")).containsText("Handling To support the bookstore's ability to provide a best-in-class online and campus bookstore experience, and to offset the rising costs of goods and services, an online handling fee of $3.00 per transaction is charged. This fee offsets additional expenses including fulfillment, distribution, operational optimization, and personalized service. No minimum purchase required. $3.00");
            assertThat(page.getByLabel("main")).containsText("Tax TBD");
            assertThat(page.getByLabel("main")).containsText("Total $167.98 167.98 $");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
            assertThat(page.getByLabel("main")).containsText("Full Name Jane Doe Email Address dpalumni@depaul.edu Phone Number +13123628000");
            assertThat(page.locator("#bnedPickupPersonForm")).containsText("Pickup Location DePaul University Loop Campus & SAIC 1 E. Jackson Boulevard, , Illinois, Chicago, 60604");
            assertThat(page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("I'll pick them up"))).isChecked();
            assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
            assertThat(page.getByLabel("main")).containsText("Handling To support the bookstore's ability to provide a best-in-class online and campus bookstore experience, and to offset the rising costs of goods and services, an online handling fee of $3.00 per transaction is charged. This fee offsets additional expenses including fulfillment, distribution, operational optimization, and personalized service. No minimum purchase required. $3.00");
            assertThat(page.getByLabel("main")).containsText("Tax TBD");
            assertThat(page.getByLabel("main")).containsText("Total $167.98 167.98 $");
            assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black Quantity: Qty: 1 $164.98");
            //Locator contin = page.getByText("Continue").first();
            //contin.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(60000));
            //contin.scrollIntoViewIfNeeded();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

            //
            page.waitForURL("https://depaul.bncollege.com/checkout/multi/payment-method/add");
            assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
            assertThat(page.getByLabel("main")).containsText("Handling To support the bookstore's ability to provide a best-in-class online and campus bookstore experience, and to offset the rising costs of goods and services, an online handling fee of $3.00 per transaction is charged. This fee offsets additional expenses including fulfillment, distribution, operational optimization, and personalized service. No minimum purchase required. $3.00");
            assertThat(page.getByLabel("main")).containsText("Tax $17.22");
            assertThat(page.getByLabel("main")).containsText("Total $185.20 185.2 $");
            assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black Quantity: Qty: 1 $164.98");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove product JBL Quantum")).click();
            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your cart is empty"))).isVisible();
            context.clearCookies();
            context.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}





