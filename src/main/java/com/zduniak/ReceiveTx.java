package com.zduniak;

import java.io.IOException;
import java.net.URISyntaxException;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.TimeBounds;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.federation.Federation;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

/**
 * Task:
 * 1. This time we will use Stellar mainnet / Horizon.
 * 2. Look for all the assets with the asset code == TON.
 * 3. For every asset found list all its bid and ask offers from the order book in the SDEX that are offered in exchange for XLM.
 * 
 */
// Details: https://www.stellar.org/developers/guides/get-started/transactions.html
public class ReceiveTx {

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		
		Network.useTestNetwork();
		
		Server server = new Server("https://horizon-testnet.stellar.org");
		final KeyPair account = KeyPair.fromAccountId("GDKAHTLZIKVHJ6GUSFJSKE4AE5GGZEFBAIYAJBT2THZGDFOJCP7NRUR6");

		PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account);
		
		// If some payments have already been handled, start the results from the
		// last seen payment. (See below in `handlePayment` where it gets saved.)
//		String lastToken = null;
//		if (lastToken != null) {
//		  paymentsRequest.cursor(lastToken);
//		}
		

		// `stream` will send each recorded payment, one by one, then keep the
		// connection open and continue to send you new payments as they occur.
		paymentsRequest.stream(new EventListener<OperationResponse>() {

		  public void onEvent(OperationResponse payment) {
			  
		    // Record the paging token so we can start from here next time.
//		    savePagingToken(payment.getPagingToken());

		    // The payments stream includes both sent and received payments. We only
		    // want to process received payments here.
		    if (payment instanceof PaymentOperationResponse) {
		      if (((PaymentOperationResponse) payment).getTo().equals(account)) {
		        return;
		      }

		      String amount = ((PaymentOperationResponse) payment).getAmount();

		      Asset asset = ((PaymentOperationResponse) payment).getAsset();
		      String assetName;
		      if (asset.equals(new AssetTypeNative())) {
		        assetName = "lumens";
		      } else {
		        StringBuilder assetNameBuilder = new StringBuilder();
		        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
		        assetNameBuilder.append(":");
		        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId());
		        assetName = assetNameBuilder.toString();
		      }

		      StringBuilder output = new StringBuilder();
		      output.append(amount);
		      output.append(" ");
		      output.append(assetName);
		      output.append(" from ");
		      output.append(((PaymentOperationResponse) payment).getFrom().getAccountId());
		      System.out.println(output.toString());
		    }

		  }
		});
		
		Thread.sleep(1000*1000L);
	}

}
