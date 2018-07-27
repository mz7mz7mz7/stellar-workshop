package com.zduniak;

import java.io.IOException;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

/**
 * Tasks:
 * 1. Amend the code so that you return all the signers of the given accounts, their weights and also XLM Balance of any Signer.
 */
// Details: https://www.stellar.org/developers/guides/get-started/create-account.html
public class ReadAllBalances {

	public static void main(String[] args) throws IOException {
		KeyPair pair = KeyPair.fromAccountId("GDKAHTLZIKVHJ6GUSFJSKE4AE5GGZEFBAIYAJBT2THZGDFOJCP7NRUR6");
		
		Server server = new Server("https://horizon-testnet.stellar.org");
		AccountResponse account = server.accounts().account(pair);
		
		System.out.println("Balances for account " + pair.getAccountId());
		
		for (AccountResponse.Balance balance : account.getBalances()) {
		  System.out.println(String.format(
		    "Type: %s, Code: %s, Balance: %s",
		    balance.getAssetType(),
		    balance.getAssetCode(),
		    balance.getBalance()));
		}
		
		
	}

}
