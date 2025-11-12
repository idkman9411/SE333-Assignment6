package playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Paths;


public class PlaywrightLLMTests {
	@Test
	public void testShoppingFlowWithVideo() {
		System.out.println("Start Playwright LLM Test");
		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
					.setHeadless(false));
			BrowserContext context = browser.newContext(new Browser.NewContextOptions()
					.setViewportSize(1280, 720)
					.setRecordVideoDir(Paths.get(System.getProperty("user.dir"), "testVideos"))
					.setRecordVideoSize(1280, 720));
			Page page = context.newPage();

			// Navigate and search
			page.navigate("https://depaul.bncollege.com/");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("earbuds");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).press("Enter");

			// Filters: Brand JBL, Color Black, Price Over $50
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
			page.getByRole(AriaRole.LISTITEM).filter(new Locator.FilterOptions().setHasText("brand JBL (12)")).getByRole(AriaRole.IMG).click();
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
			page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black (9)")).click();
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
			page.locator("label").filter(new Locator.FilterOptions().setHasText("Price Over $50 (8)")).click();

			// Open product
			page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();

			// Assertions: product name, SKU, price, description
			assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING).first()).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
			assertThat(page.getByLabel("main")).containsText("sku 668972707");
			assertThat(page.getByLabel("main")).containsText("$164.98");
			assertThat(page.getByLabel("main")).containsText("Adaptive noise cancelling allows awareness of environment when gaming on the go");

			// Add to cart and check cart
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).click();
			assertThat(page.locator("#headerDesktopView")).containsText("Cart 1 items");
			page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
			assertThat(page.getByLabel("main")).containsText("Your Shopping Cart");
			assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
			assertThat(page.getByLabel("main")).containsText("(1 Item)");
			assertThat(page.getByLabel("main")).containsText("$164.98");

			// Pickup and sidebar assertions
			page.getByText("FAST In-Store PickupDePaul").click();
			assertThat(page.getByLabel("main")).containsText("Subtotal $164.98");
			assertThat(page.getByLabel("main")).containsText("$3.00");
			assertThat(page.getByLabel("main")).containsText("Taxes TBD");
			assertThat(page.getByLabel("main")).containsText("Estimated Total $167.98");

			// Promo code
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).fill("TEST");
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Promo Code")).click();
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Promo Code")).click();
			// Wait for promo result to appear and stabilize
			assertThat(page.locator("#js-voucher-result")).containsText("The coupon code entered is not valid.");
			// Wait for network idle so any async UI updates finish before trying to navigate
			try {
				page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(10000));
			} catch (PlaywrightException e) {
				// ignore timeout; proceed to additional checks
			}
			// Small pause to let UI animations settle
			try { Thread.sleep(500); } catch (InterruptedException ie) { }
			// Ensure the Proceed To Checkout button is present/attached before clicking
			try {
				page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			} catch (PlaywrightException e) {
				// ignore — robustClick will handle retries/fallbacks
			}

			// Proceed to checkout (use robust click to avoid flakiness)
			//clickProceedToCheckout(page);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).first().click();// manually added
            page.navigate("https://depaul.bncollege.com/login/checkout");// ma
			assertThat(page.getByLabel("main")).containsText("Create Account");
			// Use robust click for Proceed As Guest (wait for Contact Information)
			//robustClick(page, page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")), "main:has-text(\"Contact Information\")");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();//manually added
            //page.navigate("https://depaul.bncollege.com/checkout/multi/delivery-address/add", setWaitUntil.WaitUntilState.DOMCONTENTLOADED); //ma

            // Contact information
			assertThat(page.getByLabel("main")).containsText("Contact Information");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).fill("Jane");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).fill("Doe");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).fill("dpalumni@depaul.edu");
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).click();
			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).fill("3123628000");

			// Sidebar checks on contact page
			assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
			assertThat(page.getByLabel("main")).containsText("$3.00");
			assertThat(page.getByLabel("main")).containsText("Tax TBD");
			assertThat(page.getByLabel("main")).containsText("Total $167.98");

			// Use robust click for Continue to ensure navigation to pickup/review completes
			//robustClick(page, page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")), "main:has-text(\"Full Name\")");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();// ma

			// Verify contact info and pickup details
			assertThat(page.getByLabel("main")).containsText("Full Name Jane Doe Email Address dpalumni@depaul.edu Phone Number +13123628000");
			assertThat(page.locator("#bnedPickupPersonForm")).containsText("Pickup Location DePaul University Loop Campus & SAIC");
			assertThat(page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("I'll pick them up"))).isChecked();
			assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
			assertThat(page.getByLabel("main")).containsText("$3.00");
			assertThat(page.getByLabel("main")).containsText("Tax TBD");
			assertThat(page.getByLabel("main")).containsText("Total $167.98");
			assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black Quantity: Qty: 1 $164.98");

			// Continue to review and assert taxes now present
			//page.waitForURL("https://depaul.bncollege.com/checkout/multi/payment-method/add");
			// Use robust click for Continue on the review -> payment transition
			//robustClick(page, page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")), "main:has-text(\"Order Subtotal\")");
            //page.getByText("Continue").click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();//ma

            assertThat(page.getByLabel("main")).containsText("Order Subtotal $164.98");
			assertThat(page.getByLabel("main")).containsText("$3.00");
			assertThat(page.getByLabel("main")).containsText("Tax $");
			assertThat(page.getByLabel("main")).containsText("Total");
			assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black Quantity: Qty: 1 $164.98");

			// Back to cart and remove
			page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove product JBL Quantum")).click();
			assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your cart is empty"))).isVisible();

			// Clean up cookies and context
			context.clearCookies();

			// Print video file location(s)
			try {
				// Wait a moment for video finalize
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				// ignore
			}
			// If any page has video, print the path
			try {
				if (page.video() != null) {
					System.out.println("Video path: " + page.video().path());
				}
			} catch (Exception e) {
				// Some contexts/pages may not expose video if closed; ignore
			}

			context.close();
		} catch (Exception e) {
			System.out.println(e);
			throw e;
		}
	}

	// Helper: optional utility to create a context with video recording enabled
	private Browser.NewContextOptions videoContextOptions() {
		return new Browser.NewContextOptions()
				.setViewportSize(1280, 720)
				.setRecordVideoDir(Paths.get("C:\\Users\\idkma\\Videos"))
				.setRecordVideoSize(1280, 720);
	}

	// Delegates to robustClick with conservative defaults for checkout transition
	private void clickProceedToCheckout(Page page) {
		robustClick(page,
				page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).first(),
				"main:has-text(\"Create Account\")", true, true, 4);
	}

	// Basic overload that preserves previous behavior
	private void robustClick(Page page, Locator locator, String waitForSelector) {
		robustClick(page, locator, waitForSelector, false, false, 3);
	}

	// Enhanced robust click with options to wait for network idle and dismiss overlays
	private void robustClick(Page page, Locator locator, String waitForSelector, boolean waitForNetworkIdle, boolean dismissOverlays, int maxAttempts) {
		Locator btn = locator;
		// Optionally ensure network is idle before attempting critical clicks
		if (waitForNetworkIdle) {
			try {
				page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(5000));
			} catch (PlaywrightException e) {
				// ignore timeout and proceed
			}
		}

		for (int attempt = 0; attempt < maxAttempts; attempt++) {
			try {
				if (dismissOverlays) {
					// Try closing common modal/overlay controls
					String[] closeSelectors = new String[]{".modal .close", ".modal-close", ".close", "button[aria-label='Close']", ".overlay .close"};
					for (String sel : closeSelectors) {
						try {
							Locator close = page.locator(sel).first();
							if (close != null && close.isVisible()) {
								close.click();
								Thread.sleep(200);
							}
						} catch (Exception ignored) {
						}
					}
					// Fallback: remove high z-index fixed/absolute elements via JS
					try {
						page.evaluate("() => { Array.from(document.querySelectorAll('*')).forEach(el => { try { const s = getComputedStyle(el); const z = parseInt(s.zIndex) || 0; if ((s.position === 'fixed' || s.position === 'absolute') && z >= 1000) { el.remove(); } } catch(e){} }); }");
						Thread.sleep(200);
					} catch (Exception ignored) {
					}
				}
				btn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
				if (!btn.isEnabled()) {
					Thread.sleep(500);
					btn = locator; // refresh reference
					continue;
				}
				btn.scrollIntoViewIfNeeded();
				btn.click();
				if (waitForSelector != null && !waitForSelector.isEmpty()) {
					try {
						page.waitForSelector(waitForSelector, new Page.WaitForSelectorOptions().setTimeout(10000));
						return; // success
					} catch (PlaywrightException e) {
						// Not yet navigated — will retry
					}
				} else {
					return;
				}
			} catch (Exception e) {
				// swallow and retry
			}
			try { Thread.sleep(700); } catch (InterruptedException ie) { }
			btn = locator; // refresh locator
		}
		// final JS fallback
		try {
			locator.evaluate("el => el.click()");
			if (waitForSelector != null && !waitForSelector.isEmpty()) {
				page.waitForSelector(waitForSelector, new Page.WaitForSelectorOptions().setTimeout(10000));
			}
			return;
		} catch (Exception e) {
			throw new RuntimeException("robustClick failed after retries", e);
		}
	}

}
