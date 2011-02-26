package rose
import spock.lang.Specification;
import spock.lang.*

public class ProgramTest extends Specification { 
	
	def	program = new Program();
	static def allItems
	static def standardItems
	
	def setupSpec() {
		def dexterityVest = new Item([name:"+5 Dexterity Vest", sellIn: 10, quality: 20])
		def agedBrie = new Item(name:"Aged Brie", sellIn: 2, quality: 0)
		def mongooseElixir = new Item(name:"Elixir of the Mongoose", sellIn: 5, quality: 7)
		def sulfuras = new Item(name:"Sulfuras, Hand of Ragnaros", sellIn: 0, quality: 80)
		def backstagePass = new Item(name:"Backstage passes to a TAFKAL80ETC concert", sellIn: 15, quality: 20)
		def conjuredManaCake = new Item(name:"Conjured Mana Cake", sellIn: 3, quality: 6)
		
		allItems = [
			dexterityVest,
			agedBrie,
			mongooseElixir,
			sulfuras,
			backstagePass,
			conjuredManaCake
		]
		standardItems = [
			dexterityVest,
			mongooseElixir,
			conjuredManaCake
		]
	}
	
	def void "sell in and quality decrease by 1 for standard items"() {
		given:
		int initialSellIn = item.sellIn
		int initialQuality = item.quality
		program.setItems([item])
		when:
		program.updateQuality()
		then:
		initialSellIn - 1 == item.sellIn
		initialQuality - 1 == item.quality
		where:
		item << standardItems
	}
	
	public void "quality degrades twice as fast after sell in date for standard items"() {
		given:
		item.sellIn = 0
		int quality = item.quality
		program.setItems([item])
		when:
		program.updateQuality();
		then:
		quality - 2 == item.quality
		where:
		item << standardItems
	}
	public void "quality is never negative"() {
		given:
		int sellIn = -1;
		int quality = 1;
		program.setItems([item]);
		when:
		100.times { program.updateQuality() }
		then:
		item.quality >= 0
		where:
		item << allItems
	}
	
	public void "quality is maxed at 50 except for Sulfuras, Hand of Ragnaros"() {
		given:
		program.setItems([item]);
		when:
		100.times { program.updateQuality() }
		then:
		item.quality <= 50
		where:
		item << allItems.findAll { item -> item.name != "Sulfuras, Hand of Ragnaros" }
	}
	
	public void "undocumented feature quality is retained for Sulfuras with quality over 50"() {
		given:
		def initialQuality = item.quality
		program.setItems([item]);
		when:
		100.times { program.updateQuality() }
		then:
		item.quality == initialQuality
		where:
		item << [new Item(name:"Sulfuras, Hand of Ragnaros", sellIn:0, quality:80)]
	}
	
	public void "aged brie increases in quality"() {
		given:
		def agedBrie = new Item(name:"Aged Brie", sellIn: 2, quality: 0)
		int quality = agedBrie.quality
		program.setItems(Arrays.asList(agedBrie));
		when:
		program.updateQuality();
		then:
		quality +1 == agedBrie.quality
	}
	
	public void "sulfuras never ages or changes quality"() {
		given:
		int sellIn = 3;
		int quality = 1;
		Item item = new Item(name:"Sulfuras, Hand of Ragnaros", sellIn:sellIn, quality:quality);
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		sellIn == item.sellIn
		quality == item.quality
	}
	
	public void "backstage passes increase in value by 1 when sell in is greater than 10 days"() {
		given:
		int sellIn = 11;
		int initialQuality = 10;
		Item item = new Item(name:"Backstage passes to a TAFKAL80ETC concert", sellIn:sellIn, quality:initialQuality);
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		initialQuality + 1 == item.quality
	}
	
	public void "backstage passes value increase in value by 2 when sell in is within 10 days"() {
		given:
		int initialQuality = 10;
		Item item = new Item(name:"Backstage passes to a TAFKAL80ETC concert", sellIn:sellIn, quality:initialQuality);
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		initialQuality + 2 == item.quality
		where:
		sellIn << (6..10 as List)
	}
	
	public void "backstage passes value increase in value by 3 when sell in is within 5 days."() {
		given:
		int initialQuality = 10;
		Item item = new Item(name:"Backstage passes to a TAFKAL80ETC concert", sellIn:sellIn, quality:initialQuality);
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		initialQuality + 3 == item.quality
		where:
		sellIn << (1..5 as List)
	}
	
	public void "backstage passes value drops to zero after sell in"() {
		given:
		int sellIn = 0;
		int initialQuality = 10;
		Item item = new Item(name:"Backstage passes to a TAFKAL80ETC concert", sellIn:sellIn, quality:initialQuality);
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		0 == item.quality
	}
}
