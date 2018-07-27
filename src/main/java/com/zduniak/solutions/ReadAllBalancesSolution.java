package com.zduniak.solutions;

import java.io.IOException;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.xdr.AssetType;

// Details: https://www.stellar.org/developers/guides/get-started/create-account.html
public class ReadAllBalancesSolution {

	public static void main(String[] args) throws IOException, InterruptedException {
		KeyPair pair = KeyPair.fromAccountId("GDKAHTLZIKVHJ6GUSFJSKE4AE5GGZEFBAIYAJBT2THZGDFOJCP7NRUR6");
		
		Server server = new Server("https://horizon-testnet.stellar.org");
		AccountResponse account = server.accounts().account(pair);
		
		System.out.println("Signers for account " + pair.getAccountId());
		
		for (AccountResponse.Signer sign : account.getSigners()) {
		  System.out.println(String.format(
		    "acc id: %s, weidght: %d",
		    sign.getAccountId(),
		    sign.getWeight()));
		  
		  	AccountResponse account2 = server.accounts().account(KeyPair.fromAccountId(sign.getAccountId()));
			
			System.out.println("Balances for account " + sign.getAccountId());
			
			for (AccountResponse.Balance balance : account2.getBalances()) {
				if ("native".equalsIgnoreCase(balance.getAssetType())) {
					System.out.println(String.format(
						    "Type: %s, Balance: %s",
						    balance.getAssetType(),
						    balance.getBalance()));
				}
			}
			
		}
	}

}
