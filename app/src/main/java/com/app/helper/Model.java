package com.app.helper;

import android.content.Context;
import com.app.wallafy.R;

import java.util.ArrayList;

public class Model {

	public static ArrayList<Item> Items;

	public static void LoadModel(Context ctx) {

		Items = new ArrayList<Item>();

		Items.add(new Item(1, R.drawable.s_cam, ctx.getString(R.string.sell_your_stuff)));
		Items.add(new Item(2, R.drawable.s_message, ctx.getString(R.string.chat)));
		Items.add(new Item(3, R.drawable.s_category, ctx.getString(R.string.categories)));
		Items.add(new Item(4, R.drawable.s_user, ctx.getString(R.string.myprofile)));
		Items.add(new Item(5, R.drawable.s_exchange, ctx.getString(R.string.myexchange)));
		Items.add(new Item(6, R.drawable.s_promotion, ctx.getString(R.string.my_promotions)));
		Items.add(new Item(7, R.drawable.s_help, ctx.getString(R.string.help)));

	}

	public static Item GetbyId(int id) {

		for (Item item : Items) {
			if (item.Id == id) {
				return item;
			}
		}

		return null;
	}

}