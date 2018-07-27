package com.zduniak.solutions;

import java.io.IOException;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.CreateAccountOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

// Details: https://www.stellar.org/developers/guides/get-started/transactions.html
public class SendNewTxSolution2 {

	public static void main(String[] args) throws IOException {
		
Network.useTestNetwork();
		
		Server server = new Server("https://horizon-testnet.stellar.org");
		
		KeyPair escrow = KeyPair.random();
		
		System.out.println("Escrow: " + new String(escrow.getAccountId()));
		System.out.println("Escrow.secret: " + new String(escrow.getSecretSeed()));

		KeyPair issuer = KeyPair.fromSecretSeed("SC6SS3JVQF2T6JWR6OJX6LCDZF5BBWW7HSEXXH4AURATTYGS5GAC2634");
		KeyPair client = KeyPair.fromAccountId("GDU4DSSOVSZTS47JE66P6KPGRF7RU3P222MWSXNEHKPUIPW7PI3PPYAJ");
		Asset eur = Asset.createNonNativeAsset("EUR", issuer);
		
		Transaction tx;
		SubmitTransactionResponse response;
		
		tx = new Transaction.Builder(server.accounts().account(issuer))
		        .addOperation(new CreateAccountOperation.Builder(escrow, "3").build())
		        .addOperation(new ChangeTrustOperation.Builder(eur, "9999999").setSourceAccount(escrow).build())
		        .addOperation(new PaymentOperation.Builder(escrow, eur, "1").build())
		        .addOperation(new SetOptionsOperation.Builder().setMasterKeyWeight(0).setSigner(client.getXdrSignerKey(), 1).setSourceAccount(escrow).build())
		        .build();
		tx.sign(issuer);
		tx.sign(escrow);
		
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());
		
		// TODO: task: try now to withdraw that 1 EUR from the escrow account
		
	}

}
