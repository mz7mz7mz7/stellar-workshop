package com.zduniak;

import org.stellar.sdk.KeyPair;

// Details: https://www.stellar.org/developers/guides/get-started/create-account.html
public class CreateNewAccount {

	public static void main(String[] args) {
		
		KeyPair pair = KeyPair.random();

		System.out.println(new String(pair.getSecretSeed()));
		System.out.println(pair.getAccountId());
	}

}
