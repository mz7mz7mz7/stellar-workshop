package com.zduniak.solutions;

import java.io.IOException;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

/**
 * Accounts:
 *  S1:
	Public Key	GDKAHTLZIKVHJ6GUSFJSKE4AE5GGZEFBAIYAJBT2THZGDFOJCP7NRUR6
	Secret Key	SC6SS3JVQF2T6JWR6OJX6LCDZF5BBWW7HSEXXH4AURATTYGS5GAC2634
	
	S2:
	Public Key	GBU5SBGAKORB34TETBOWJLUZIVYYT53TENE266FY6KYB2TEJNELTG4VN
	Secret Key	SCHCXELZF4HNAAGW576FFCJLCQDK4WD2ONCJHMVFGU5IO5CYOIDDJCNM
	
	D1:
	Public Key	GDU4DSSOVSZTS47JE66P6KPGRF7RU3P222MWSXNEHKPUIPW7PI3PPYAJ
	Secret Key	SDPGEFFMWKXJVG7ZBD2HWULTRP4ZPDZCEBMWK6COC6HA7UJVI6TTQUQ2
	
	D2:
	Public Key	GAXHJRX7NLDYLAJ3YGO24YVLWILZ4MAIBU5RWLVVMAJG3Z7AJYTPCQ4W
	Secret Key	SCE2RT2NAONCXHSBGEOK4EDHI3K6TA6KGR44E3GBP3UUXP4POA3RRRHB
 *
 */
// Details: https://www.stellar.org/developers/guides/get-started/transactions.html
public class SendNewTxSolution {

	public static void main(String[] args) throws IOException {
		
		Network.useTestNetwork();
		
		Server server = new Server("https://horizon-testnet.stellar.org");

		KeyPair s1 = KeyPair.fromSecretSeed("SC6SS3JVQF2T6JWR6OJX6LCDZF5BBWW7HSEXXH4AURATTYGS5GAC2634");
		KeyPair s2 = KeyPair.fromSecretSeed("SCHCXELZF4HNAAGW576FFCJLCQDK4WD2ONCJHMVFGU5IO5CYOIDDJCNM");
		KeyPair d1 = KeyPair.fromSecretSeed("SDPGEFFMWKXJVG7ZBD2HWULTRP4ZPDZCEBMWK6COC6HA7UJVI6TTQUQ2");
		KeyPair d2 = KeyPair.fromSecretSeed("SCE2RT2NAONCXHSBGEOK4EDHI3K6TA6KGR44E3GBP3UUXP4POA3RRRHB");
		
		Transaction tx;
		SubmitTransactionResponse response;
		
		// Setup USD trust lines between D1 and S1 accounts:
		tx = new Transaction.Builder(server.accounts().account(d1))
		        .addOperation(new ChangeTrustOperation.Builder(Asset.createNonNativeAsset("USD", s1), "9999999").build())
		        .build();
		tx.sign(d1);
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());
		
		// Setup EUR trust lines between D2 and S2 accounts:
		tx = new Transaction.Builder(server.accounts().account(d2))
		        .addOperation(new ChangeTrustOperation.Builder(Asset.createNonNativeAsset("EUR", s2), "9999999").build())
		        .build();
		tx.sign(d2);
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());
		
/* Not needed / Not used, but nice to see that s1's EUR balance is not depleted:
		// Setup EUR trust lines between S1 and S2 accounts:
		tx = new Transaction.Builder(server.accounts().account(s1))
		        .addOperation(new ChangeTrustOperation.Builder(Asset.createNonNativeAsset("EUR", s2), "9999999").build())
		        .build();
		tx.sign(s1);
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());

		
		// Send EUR from s2 to s1:
		tx = new Transaction.Builder(server.accounts().account(s2))
		        .addOperation(new PaymentOperation.Builder(s1, Asset.createNonNativeAsset("EUR", s2), "100").build())
		        .build();
		tx.sign(s2);
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());
*/
		
		// Now issue the USD and EUR at the same time sending it to the respective accounts:
		tx = new Transaction.Builder(server.accounts().account(s1))
		        .addOperation(new PaymentOperation.Builder(d1, Asset.createNonNativeAsset("USD", s1), "1").build())
		        .addOperation(new PaymentOperation.Builder(d2, Asset.createNonNativeAsset("EUR", s2), "2").setSourceAccount(s2).build())
		        .build();
		tx.sign(s2);
		tx.sign(s1);
		response = server.submitTransaction(tx);
		System.out.println("Success: " + response.isSuccess());
		
	}

}
