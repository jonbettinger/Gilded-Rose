package rose

import groovy.lang.ExpandoMetaClass;

public class Program {
	public Program() {
		assignUpdateLogic "+5 Dexterity Vest", standardUpdateLogic
		assignUpdateLogic "Aged Brie", agedBrieUpdateLogic
		assignUpdateLogic "Elixir of the Mongoose", standardUpdateLogic
		assignUpdateLogic "Sulfuras, Hand of Ragnaros", sulfurasUpdateLogic
		assignUpdateLogic "Backstage passes to a TAFKAL80ETC concert", backstagePassUpdateLogic
		assignUpdateLogic "Conjured Mana Cake", standardUpdateLogic
	}
	
	private def standardUpdateLogic = {->
		if (quality > 0) {
			quality--
		}
		sellIn--
		if (sellIn < 0 && quality > 0) {
			quality--
		}
	}
	
	private def agedBrieUpdateLogic = {->
		if (quality < 50) {
			quality++
		}
		sellIn--
		if (sellIn < 0 && quality < 50) {
			quality++
		}
	}
	
	private def sulfurasUpdateLogic = {-> 
		sellIn-- 
	}
	
	private def backstagePassUpdateLogic = {->
		if (quality < 50) {
			quality++
			if (sellIn < 11 && quality < 50) {
				quality++
			}
			if (sellIn < 6 && quality < 50) {
				quality++
			}
		}
		sellIn--
		if (sellIn < 0) {
			quality = 0
		}
	}
	
	private def assignUpdateLogic(name, updateClosure) {
		def metaClass = new ExpandoMetaClass(Item)
		metaClass.updateItemQuality = updateClosure
		metaClass.initialize()
		items.find { item -> item.name == name }.metaClass = metaClass
	}
	
	void updateQuality() {
		items.each { item ->
			item.updateItemQuality()
		}
	}
	
	List<Item> items = [
		new Item ( name: "+5 Dexterity Vest", sellIn: 10, quality: 20 ),
		new Item ( name: "Aged Brie", sellIn: 2, quality: 0 ),
		new Item ( name: "Elixir of the Mongoose", sellIn: 5, quality: 7 ),
		new Item ( name: "Sulfuras, Hand of Ragnaros", sellIn: 0, quality: 80 ),
		new Item ( name: "Backstage passes to a TAFKAL80ETC concert", sellIn: 15, quality: 20 ),
		new Item ( name: "Conjured Mana Cake", sellIn: 3, quality: 6 )
	]
}

class Item {
	String name
	int sellIn
	int quality
	
	String toString() {
		"Item name: ${name},sellIn: ${sellIn},quality: ${quality}"
	}
}