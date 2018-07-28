package com.zduniak;

import java.io.IOException;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

/**
 * Requirements for Task 1:
 * 1. We have accounts S1, S2, D1, D2 .
 * 2. Create a transaction with two PaymentOperations (S1 pays the txn fee):
 *   a) S1 sending 10 USD to D1, from its own balance.
 *   b) S2 sending 20 EUR to D2, from S2's balance.
 * 3. Sequence number is increased only on account S1.
 * 4. All the preparatory transactions (TrustLimit setup, initial distribution of the assets should happen in separate transactions).
 * 5. Use friendly bot / laboratory to create s1, s2, d1, d2.
 * 
 * TODO: Note that this technique (called Channels) can be used to implement transaction sending at a high rate from the decoupled nodes/servers.
 * More on it here: https://www.stellar.org/developers/guides/channels.html
 * 
 *  Requirements for Task 2:
 *  1. You have access to the prv key of the Issuing account A (asset: EUR).
 *  2. You have also access to your clients public key of account C.
 *  3. The task:
 *    In one single transaction create the new Stellar account and fund it with the 1 EUR so that *only* C can spend it.  
 */
// Details: https://www.stellar.org/developers/guides/get-started/transactions.html
public class SendNewTx {

	public static void main(String[] args) throws IOException {
		
		Network.useTestNetwork();
		
		Server server = new Server("https://horizon-testnet.stellar.org");

		KeyPair source = KeyPair.fromSecretSeed("SC6SS3JVQF2T6JWR6OJX6LCDZF5BBWW7HSEXXH4AURATTYGS5GAC2634");
		KeyPair destination = KeyPair.fromAccountId("GA2C5RFPE6GCKMY3US5PAB6UZLKIGSPIUKSLRB6Q723BM2OARMDUYEJ5");

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or there was another error.
		server.accounts().account(destination);

		// If there was no error, load up-to-date information on your account.
		AccountResponse sourceAccount = server.accounts().account(source);

		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
		        .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "0.001").build())
		        // A memo allows you to add your own metadata to a transaction. It's
		        // optional and does not affect how Stellar treats the transaction.
		        .addMemo(Memo.text("Test Transaction"))
		        .build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);
		

		// And finally, send it off to Stellar!
		try {
		  SubmitTransactionResponse response = server.submitTransaction(transaction);
		  System.out.println("Success: " + response.isSuccess());
		  System.out.println(response.getLedger());
		} catch (Exception e) {
		  System.out.println("Something went wrong!");
		  System.out.println(e.getMessage());
		  // If the result is unknown (no response body, timeout etc.) we simply resubmit
		  // already built transaction:
		  // SubmitTransactionResponse response = server.submitTransaction(transaction);
		}
	}

}
