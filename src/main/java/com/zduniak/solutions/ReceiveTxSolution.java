package com.zduniak.solutions;

import java.io.IOException;
import java.net.URISyntaxException;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.OrderBookResponse;
import org.stellar.sdk.responses.OrderBookResponse.Row;
import org.stellar.sdk.responses.Page;


// Details: https://www.stellar.org/developers/guides/get-started/transactions.html
public class ReceiveTxSolution {

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		
		Network.useTestNetwork();
		
		Server server = new Server("https://horizon.stellar.org");

		String assetCode = "TON";
		Page<AssetResponse> page = server.assets().assetCode(assetCode).execute();
		for (AssetResponse a : page.getRecords()) {
			String issuer = a.getAssetIssuer();
			System.out.println(issuer);
			Asset asset = Asset.createNonNativeAsset(assetCode, KeyPair.fromAccountId(issuer));
			OrderBookResponse orders = server.orderBook().buyingAsset(asset).sellingAsset(new AssetTypeNative()).execute();
			for (Row row : orders.getAsks()) {
				System.out.println("Sell offer of " + row.getAmount() + " " + assetCode + " for " + row.getPrice() + " XLM");
			}
			for (Row row : orders.getBids()) {
				System.out.println("Buy offer of " + row.getAmount() + " " + assetCode + " for " + row.getPrice() + " XLM");
			}
			
		}
		
	}

}
