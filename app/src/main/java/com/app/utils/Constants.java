package com.app.utils;/****************** @author 'Hitasoft Technologies'* * Description:  * This class is used for storing static data.** Revision History:* Version 1.0 - Initial Version******************/import android.content.SharedPreferences;import android.content.SharedPreferences.Editor;public class Constants { 	public static SharedPreferences pref;	public static Editor editor;	public static SharedPreferences filpref;	public static Editor fileditor;		/** FaceBook App id **/	public static final String App_ID = "1833922286827987";	/** Twitter App id **/	public static final String CONSUMER_KEY = "sWSBOKFnKO0nwsNuNTZL78bC0";	public static final String CONSUMER_SECRET = "VBgvAL6MxYiGVGzoHrNckw3BFLaVXBe00SSG9y6F1tILRsC2tf";	/** Push notification key **/	//public static final String SENDER_ID = "446186285813";	public static final String SENDER_ID = "600632316387";	public static String REGISTER_ID="";	public static String ANDROID_ID="";    // PUSH API KEY AIzaSyAK--ZYqqD8OjueQb_YB98llQMFIGkCYyw //		/** Api  **/	public static final String URL = "http://oferfy.com/api/service?ws=1";	public static final String NAMESPACE = "urn:ApiControllerwsdl";	public static final String url = "http://oferfy.com/";	public static final String SOCKET_URL = "http://oferfy.com:8080/";	public static final String API_LOGIN = "login";	public static final String API_SOCIAL_LOGIN = "sociallogin";	public static final String API_SIGNUP = "signup";	public static final String API_FORGET_PASSWORD = "forgetpassword";	public static final String API_HOME = "getitems";	public static final String API_CATEGORY = "getcategory";	public static final String API_POST_COMMENTS = "postcomment";	public static final String API_CHANGE_PASSWORD = "changepassword";	public static final String API_SEARCH = "getitems";	public static final String API_PROFILE = "profile";	public static final String API_POST_PRODUCT = "addproduct";	public static final String API_ITEM_LIKE = "itemlike";	public static final String API_GET_SETTINGS = "getsettings";	public static final String API_MYORDERS = "myorders";	public static final String API_MYSALES = "mysales";	public static final String API_CHANGE_ORDER_STATUS = "orderstatus";	public static final String API_GET_TRACK_DETAILS = "gettrackdetails";	public static final String API_SEARCH_ITEM = "searchbyitem";	public static final String API_GET_COMMENTS = "getcomments";	public static final String API_PRODUCT_BEFORE_ADD = "productbeforeadd";	public static final String API_UPLOAD_ITEM = url + "api/uploadimage/item";	public static final String API_REMOVE_ITEM_IMAGE = url + "api/deleteimage/item";	public static final String API_REMOVE_USER_IMAGE = url + "api/deleteimage/avatars";	public static final String API_GET_SHIPPING = "getShippingAddress";	public static final String API_ADD_SHIPPING = "addshipping";	public static final String API_GET_PAYMENT_DETAILS = "payment";	public static final String API_GET_HASHTAG = url + "api/gethashtag/";	public static final String API_GET_ATTAG = url + "api/getatuser/";	public static final String API_HASHTAG = url + "api/hashtag/";	public static final String API_GET_ITEM = url + "api/home/item";	public static final String API_CHANGE_USER_IMG = url + "api/userimagechange";	public static final String API_MORE_ITEM = "getitems";	public static final String API_CREATE_EXCHANGE= "createexchange";	public static final String API_MY_EXCHANGE= "myexchanges";	public static final String API_CHANGE_EXCHANGE= "exchangestatus";	public static final String API_MESSAGE= "messages";	public static final String API_GET_CHAT= "getchat";	public static final String API_SEND_CHAT= "sendchat";	public static final String API_GET_CHAT_ID= "getchatid";	public static final String API_UPDATE_VIEW= "updateview";	public static final String API_APPLY_COUPON= "applycoupon";	public static final String API_CHECK_SHIPPING= "shippingavail";	public static final String API_PUSH_REGISTER= "adddeviceid";	public static final String API_PUSH_UNREGISTER= "pushsignout";	public static final String API_REPORT_ITEM= "reportitem";	public static final String API_FOLLOW= "Followuser";	public static final String API_UNFOLLOW= "Unfollowuser";	public static final String API_LIKED = "liked";	public static final String API_HELP = "helppage";	public static final String API_GET_PROMOTION = "getpromotion";	public static final String API_GET_CLIENTTOKEN = "braintreeClientToken";	public static final String API_PAY_FOR_PROMOTION = "processingPayment";	public static final String API_MY_PROMOTIONS = "mypromotions";	public static final String API_FOLLOWERS = "Followersdetails";	public static final String API_FOLLOWINGS = "Followingdetails";	public static final String API_NOTIFICATIONS = "notification";	public static final String API_GET_FOLLOWER_ID = "Getfollowerid";	public static final String API_GET_LIKED_ID = "Getlikedid";	public static final String API_SOLD_ITEM = "solditem";	public static final String API_DELETE_PRODUCT = "deleteproduct";	public static final String API_SEND_OFFER_REQ = "Sendofferreq";	public static final String API_EDIT_PROFILE= "Editprofile";	public static final String API_GET_OTP= "Getotp";	public static final String API_CONFIRM_OTP= "Confirmotp";	public static final String API_UPLOAD_USER = url + "api/uploadimage/user";	public static final String API_CHECK_PROMOTION= "Checkpromotion";	/** login details to access json from soap **/	public static final String SOAP_USERNAME = "api_username";	public static final String SOAP_PASSWORD = "api_password";		public static final String SOAP_USERNAME_VALUE = "joySale";	public static final String SOAP_PASSWORD_VALUE = "0RWK9XM8";	/** Static Keywords for Json Parsing **/		// Home Page keys //	public static final String TAG_STATUS = "status";	public static final String TAG_RESULT = "result";	public static final String TAG_ITEMS = "items";	public static final String TAG_ID = "id";	public static final String TAG_TITLE = "item_title";	public static final String TAG_PROURL="product_url";	public static final String TAG_ITEM_DES = "item_description";	public static final String TAG_ITEM_CONDITION = "item_condition";	public static final String TAG_PRICE = "price";	public static final String TAG_SELLERID = "seller_id";	public static final String TAG_SELLERNAME = "seller_name";	public static final String TAG_SELLERIMG = "seller_img";	public static final String TAG_CURRENCY_CODE = "currency_code";	public static final String TAG_LIKECOUNT = "likes_count";	public static final String TAG_BUYTYPE = "buy_type";	public static final String TAG_COMMENTCOUNT = "comments_count";	public static final String TAG_VIEWCOUNT = "views_count";	public static final String TAG_LIKED = "liked";	public static final String TAG_POSTED_TIME = "posted_time";	public static final String TAG_LATITUDE = "latitude";	public static final String TAG_LONGITUDE = "longitude";	public static final String TAG_BEST_OFFER = "best_offer";	public static final String TAG_PHOTOS = "photos";	public static final String TAG_ITEM_URL_350 = "item_url_main_350";	public static final String TAG_ITEM_URL_ORG = "item_url_main_original";	public static final String TAG_RECENTCOMMENT = "recent_comment";	public static final String TAG_COMMENTID = "comment_id";	public static final String TAG_COMMENT = "comment";	public static final String TAG_USERID = "user_id";	public static final String TAG_USERIMG = "user_img";	public static final String TAG_USERNAME = "user_name";	public static final String TAG_COMMENTTIME = "comment_time";	public static final String TAG_QUANTITY = "quantity";	public static final String TAG_Name = "name";	public static final String TAG_Qty = "qty";	public static final String TAG_COLOR = "color";	public static final String TAG_LOCATION= "location";	public static final String TAG_ITEM_STATUS = "item_status";	public static final String TAG_LAST_REPLIED= "last_repliedto";	public static final String TAG_SUBCATEGORYID = "subcat_id";	public static final String TAG_SUBCATEGORYNAME = "subcat_name";	public static final String TAG_SHIPPING_DETAIL = "shipping_detail";	public static final String TAG_USER_NAME = "username";	public static final String TAG_DETAILS = "details";	public static final String TAG_PAYPALID = "paypal_id";	public static final String TAG_REPORT = "report";	public static final String TAG_WIDTH = "width";	public static final String TAG_HEIGHT = "height";	public static final String TAG_PROMOTION_TYPE = "promotion_type";	public static final String TAG_EXCHANGE_BUY = "exchange_buy";	public static final String TAG_MAKE_OFFER = "make_offer";	public static final String TAG_SELLER_USERNAME = "seller_username";	public static final String TAG_FACEBOOK_VERIFICATION = "facebook_verification";	public static final String TAG_MOBILE_VERIFICATION = "mobile_verification";	public static final String TAG_EMAIL_VERIFICATION = "email_verification";	public static final String TAG_PRODUCT_CONDITION = "product_condition";		// notification keys//	public static final String TAG_NOTIFICATION="notifications";	public static final String TAG_USERIMAGE="user_image";	public static final String TAG_ITEMIMAGE="item_image";		// message keys //	public static final String TAG_MESSAGEID= "message_id";	public static final String TAG_MESSAGETIME = "message_time";		// exchange keys //	public static final String TAG_EXCHANGE= "exchange";	public static final String TAG_EXCHANGEID= "exchange_id";	public static final String TAG_EXCHANGETIME= "exchange_time";	public static final String TAG_EXCHANGERNAME= "exchanger_name";    public static final String TAG_EXCHANGERUSERNAME= "exchanger_username";	public static final String TAG_EXCHANGERID= "exchanger_id";	public static final String TAG_EXCHANGERIMG= "exchanger_image";	public static final String TAG_REQUEST_BY_ME= "request_by_me";	public static final String TAG_MYPRODUCT= "my_product";	public static final String TAG_EXCHANGEPRODUCT= "exchange_product";			// search page keys //	public static final String TAG_CATEGORY = "category";	public static final String TAG_CATEGORYID = "category_id";	public static final String TAG_CATEGORYNAME = "category_name";	public static final String TAG_CATEGORYIMG = "category_img";	public static final String TAG_SUBCATEGORY = "subcategory";	public static final String TAG_SUBID = "sub_id";	public static final String TAG_SUBNAME = "sub_name";	// Promotions Page //	public static final String TAG_URGENT = "urgent";	public static final String TAG_CURRENCY_SYM = "currency_symbol";	public static final String TAG_NAME = "name";	public static final String TAG_DAYS = "days";	public static final String TAG_TOKEN = "token";	public static final String TAG_PROMOTION_NAME = "promotion_name";	public static final String TAG_PAID_AMOUNT = "paid_amount";	public static final String TAG_UPTO = "upto";	public static final String TAG_TRANSACTION_ID = "transaction_id";	public static final String TAG_ITEM_IMAGE = "item_image";	public static final String TAG_ITEM_ID = "item_id";	public static final String TAG_COUNTRYNAME = "country_name";	public static final String TAG_COUNTRYID = "country_id";	// chat keys //	public static final String TAG_CHAT_ID = "chat_id";	public static final String TAG_CHATS = "chats";	public static final String TAG_RECEIVER = "receiver";	public static final String TAG_SENDER = "sender";	public static final String TAG_CHATTIME = "chatTime";	public static final String TAG_CHAT_URL = "chat_url";	public static final String TAG_OFFER_PRICE = "offer_price";		// people keys //	public static final String TAG_PEOPLE = "people";	public static final String TAG_userId = "userId";	public static final String TAG_userName = "userName";	public static final String TAG_userImage = "userImage";	public static final String TAG_fullName = "fullName";		// alert keys //	public static final String TAG_ALERTS = "alerts";	public static final String TAG_TYPE = "type";	public static final String TAG_EVENTTIME = "event_time";	public static final String TAG_FOLLOWERIMG = "follower_img";	public static final String TAG_MESSAGE = "message";	public static final String TAG_ITEMID = "item_id";	public static final String TAG_COLLECTION_NAME = "collection_name";	public static final String TAG_ITEM_150 = "item_url_150";		// news keys //	public static final String TAG_NEWS = "news";	public static final String TAG_STORY = "story";	public static final String TAG_STATUS_IMG = "status_image";		// collection keys //	public static final String TAG_COLLECTIONS = "collections";	public static final String TAG_COLLECTION_DES = "collection_description";	public static final String TAG_FOLLOWER_COUNT = "follower_count";	public static final String TAG_PRODUCT_COUNT = "product_count";			// contacts keys  //	public static final String TAG_CONTACTS = "contacts";	public static final String TAG_FULLNAME = "full_name";		// cart keys //	public static final String TAG_DATA = "data";	public static final String TAG_MERCHANT_NAME = "merchant_name";	public static final String TAG_MERCHANT_ID = "merchant_id";	public static final String TAG_SHIPPING_TIME = "shipping_time";	public static final String TAG_ITEM_SIZE = "item_size";	public static final String TAG_SHIPPING_COST = "shipping_cost";	public static final String TAG_SHIPPING_PRICE = "shipping_price";	public static final String TAG_TOTAL_COST = "total_cost";	public static final String TAG_GRAND_TOTAL = "grand_total";	public static final String TAG_PRODUCTS = "products";	public static final String TAG_ITEM_NAME = "item_name";	public static final String TAG_ITEM_URL = "item_url";	public static final String TAG_ITEM_PRICE = "item_price";	public static final String TAG_ITEM_COUNT = "item_count";	public static final String TAG_ITEM_TOTAL_COUNT = "item_total_count";	public static final String TAG_ITEM_APPROVE = "item_approve";	public static final String TAG_COUPON_VALUE = "coupon_value";	public static final String TAG_COUPON_ID = "coupon_id";	public static final String TAG_MAX_AMOUNT = "max_amount";	public static final String TAG_DISCOUNT_AMOUNT = "discount_amount";		// my orders keys //	public static final String TAG_ORDER_ID="orderid";	public static final String TAG_SALEDATE="saledate";	public static final String TAG_ORDERITEMS="orderitems";	public static final String TAG_ORDERIMG="orderImage";	public static final String TAG_ITEMIMG="itemimage";	public static final String TAG_ITEMNAME="itemname";	public static final String TAG_QTY="quantity";	public static final String TAG_UPRICE="unitprice";	public static final String TAG_SIZE="size";	public static final String TAG_SYMBOL="cSymbol";		public static final String TAG_Shipping="shippingaddress";	public static final String TAG_name="name";	public static final String TAG_country="country";	public static final String TAG_state="state";	public static final String TAG_city="city";	public static final String TAG_phone="phone";	public static final String TAG_countrycode="countrycode";	public static final String TAG_address1="address1";	public static final String TAG_zipcode="zipcode";		public static final String TAG_trackingdetail="trackingdetails";	public static final String TAG_trackid="trackingid";	public static final String TAG_couriername="couriername";	public static final String TAG_courierservice="courierservice";	public static final String TAG_notes="notes";	public static final String TAG_shipmentdate="shippingdate";	/*  * Admin Messages  */	public static final String TAG_USER_ID = "userId";	public static final String TAG_DAYTIME = "dayAndTime";	public static final String TAG_COMMENTS = "comments";	public static final String TAG_EXCHANGEMESSAGE = "exchangemessage";	public static final String TAG_NOTIFYMESSAGE = "notifymessage";	public static final String TAG_ADMINTYPE = "admin_type";	public static final String TAG_USERIMAGE_M = "userimage";	public static final String TAG_ADMIN_IMAGE = "admin_image";	public static final String TAG_ITEM_TITLE="item_title";	public static final String TAG_NOTIFICATION_ID = "notification_id";	public static final String TAG_TITLE_M = "title";}