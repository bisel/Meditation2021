package com.soulfriends.meditation.netservice;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class NetServiceBillingManager {
    private NetServiceBillingManager() {
    }

    private static class instanceclass {
        private static final NetServiceBillingManager instance = new NetServiceBillingManager();
    }

    public static NetServiceBillingManager getinstance(){
        return NetServiceBillingManager.instanceclass.instance;
    }

    private Activity mActivity = null;
    private BillingClient billingClient = null;
    public  List<SkuDetails> mskuDetailsList = new ArrayList<SkuDetails>();

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                   // handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    public void init(Activity activity){
        mActivity = activity;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build();
    }

    public void connect() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("billing","connect billing");
                    NetServiceBillingManager.getinstance().showBuyProductInfo("payment01");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("billing","disconnect billing");
            }
        });
    }

    private SkuDetails userSkuDetails;

    public void showBuyProductInfo(String itemName) {
        // itemName들을 적는다. 그리고 해당 아이템의 Detail정보를 얻는다.
        List<String> skuList = new ArrayList<>();
        skuList.add("payment01");
        skuList.add("payment12");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);  // Sku

        // 비동기 처리
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        //연결을 못함.
                        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK){
                            return;
                        }

                        //상품정보를 가저오지 못함 -
                        if (skuDetailsList == null){
                            return;
                        }

                        //상품가저오기 : 정기결제상품 하나라서 한개만 처리함.
                        try {
                            mskuDetailsList = skuDetailsList;

                            for (SkuDetails skuDetails : skuDetailsList) {
                                String title = skuDetails.getTitle();
                                String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                userSkuDetails = skuDetails;
                                if (itemName.equals(sku)){
                                    showBilling(skuDetails);
                                }
                            }


                        }
                        catch (Exception e){
                           // L.e("## 리스트 가저오기 오류" + e.toString());
                        }

                    }
        });

    }

//    public void buyItem(Activity activity) {
//        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                .setSkuDetails(skuDetails)
//                .build();
//        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
//    }

    private void showBilling(SkuDetails skuDetails) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        // launchBillingFlow() 호출에 성공하면 시스템에서 Google Play 구매 화면을 표시합니다. 그림 1은 정기 결제 구매 화면을 나타낸 것입니다.
        int responseCode =  billingClient.launchBillingFlow(mActivity,flowParams).getResponseCode();

        Log.d("showBilling","ShowBilling ERROR");
    }

    private void checkInAppbilling(){
//        LifeInAppBilling.checkPurchaseAppCache(
//                this
//                ,AppConfig.BILLING_ITEM
//                ,new CheckPurchaseCallback() {
//                    @Override
//                    public void onSubscribe(BillingStateVO bsVO) {
//                        bsVO.printVO();
//
//                        if (!bsVO.isAutoRenewing()){
//                            //정책에 따라 자동갱신안한경우 자동갱신하도록 유도
//                        }
//                    }
//                    @Override
//                    public void onSubscribePending() {
//                        //보류
//                    }
//                    @Override
//                    public void onSubscribeEND(BillingStateVO bsVO) {
//                        bsVO.printVO();
//                    }
//
//                    @Override
//                    public void onNotPurchased(String msg) {
//
//                    }
//
//                    @Override
//                    public void onUnstableServer() {
//
//                    }
//                });
    }

    public void checkPurchase(Activity activity) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                //connection success
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //check query
                    Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);

                    int list = purchasesResult.getPurchasesList().size();

                    //앱스토어에서 정기결제 구독한 사람
                    if (list >= 0){
                        for (Purchase purchase : purchasesResult.getPurchasesList()){

                            String resultVal = purchase.getOriginalJson();

                            if(resultVal.equals("test")){

                            }
                           // Gson gson = new Gson();
                            //BillingStateVO bsVO = gson.fromJson(purchase.getOriginalJson(), BillingStateVO.class);
                            //boolean isPerchase = false;
                            //isPerchase =  pakageName.equals(bsVO.getPackageName())&&itemName.equals(bsVO.getProductId());
                            //해당앱 정기 구독한 사람
//                            if (isPerchase){
//                                //정기구독 유지중인 사람
//                                if (bsVO.isAutoRenewing()){
//
//                                }
//                                //정기구독 취소한사람
//                                else {
//
//                                }
//                            }
//                            //해당앱 정기구독 구입한적이 없는 사람
//                            else {
//
//                            }
                        }
                    }
                    //앱스토어에서 정기결제 한번도 한적 없는 사람
                    else {
                    }

                }
                //connection fail
                else{
                    Log.d("billing",Integer.toString(billingResult.getResponseCode()));
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d("billing","onBillingServiceDisconnected Error");
            }
        });
    }

    public interface CheckPurchaseCallback{
        void onSubscribe(BillingStateVO bsVO);
        void onSubscribePending();
        void onSubscribeEND(BillingStateVO bsVO);
        void onNotPurchased(String msg);
        void onUnstableServer();
    }

    // 상태조회시 넘어오는 값들.
    public class BillingStateVO {
        private String orderId;
        private String packageName;
        private String productId;
        private long purchaseTime;//long
        private String purchaseState;//int
        private String purchaseToken;
        private boolean autoRenewing;//boolean
        private boolean acknowledged;//boolean

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String pakageName) {
            this.packageName = pakageName;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public long getPurchaseTime() {
            return purchaseTime;
        }

        public void setPurchaseTime(long purchaseTime) {
            this.purchaseTime = purchaseTime;
        }

        public String getPurchaseState() {
            return purchaseState;
        }

        public void setPurchaseState(String purchaseState) {
            this.purchaseState = purchaseState;
        }

        public String getPurchaseToken() {
            return purchaseToken;
        }

        public void setPurchaseToken(String purchaseToken) {
            this.purchaseToken = purchaseToken;
        }

        public boolean isAutoRenewing() {
            return autoRenewing;
        }

        public void setAutoRenewing(boolean autoRenewing) {
            this.autoRenewing = autoRenewing;
        }

        public boolean isAcknowledged() {
            return acknowledged;
        }

        public void setAcknowledged(boolean acknowledged) {
            this.acknowledged = acknowledged;
        }


        public void printVO(){
            Log.i("purchase","\n## orderId : "+orderId +
                    "\n pakageName : "+packageName +
                    "\n productId : "+productId +
                    "\n purchaseTime : "+purchaseTime +
                    "\n purchaseState : "+purchaseState +
                    "\n purchaseToken : "+purchaseToken +
                    "\n autoRenewing : "+autoRenewing +
                    "\n acknowledged : "+acknowledged +"\n"
            );
        }
    }
}
