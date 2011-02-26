package rose
import spock.lang.Specification;
import spock.lang.*

public class ProgramTest extends Specification { 
	
	static def program
	static def allItems
	static def standardItems
	
	def setup() {
		program = new Program()
		allItems = program.items
		standardItems = allItems.findAll { ["+5 Dexterity Vest", "Elixir of the Mongoose", "Conjured Mana Cake"].contains(it.name) }
	}
	
	def setupSpec() {
		program = new Program()
		allItems = program.items
		standardItems = allItems.findAll { ["+5 Dexterity Vest", "Elixir of the Mongoose", "Conjured Mana Cake"].contains(it.name) }
	}
	
	def void "sell in and quality decrease by 1 for standard items"() {
		given:
		int initialSellIn = item.sellIn
		int initialQuality = item.quality
		program.items = [item]
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
		when:
		program.setItems([item])
		100.times { program.updateQuality() }
		then:
		item.quality >= 0
		where:
		item << allItems
	}
	
	public void "quality is maxed at 50 except for Sulfuras, Hand of Ragnaros"() {
		given:
		when:
		program.setItems([item])
		100.times { program.updateQuality() }
		then:
		item.quality <= 50
		where:
		item << allItems.findAll { item -> item.name != "Sulfuras, Hand of Ragnaros" }
	}
	
	public void "undocumented feature quality is retained for Sulfuras with quality over 50"() {
		given:
		def initialQuality = item.quality
		when:
		program.setItems([item])
		100.times { program.updateQuality() }
		then:
		item.quality == initialQuality
		where:
		item << allItems.findAll { it.name == "Sulfuras, Hand of Ragnaros" }
	}
	
	public void "aged brie increases in quality"() {
		given:
		final def agedBrie = allItems.find { it.name == "Aged Brie" }
		int quality = agedBrie.quality
		program.items = [agedBrie]
		when:
		program.updateQuality();
		then:
		quality + 1 == agedBrie.quality
	}
	
	public void "sulfuras never ages or changes quality"() {
		given:
		Item item = allItems.find { item -> item.name == "Sulfuras, Hand of Ragnaros" }
		int sellIn = item.sellIn;
		int quality = item.quality;
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
		Item item = allItems.find {it.name == "Backstage passes to a TAFKAL80ETC concert"};
		item.sellIn = sellIn
		item.quality = initialQuality
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		initialQuality + 1 == item.quality
	}
	
	public void "backstage passes value increase in value by 2 when sell in is within 10 days"() {
		given:
		int initialQuality = 10;
		Item item = allItems.find {it.name == "Backstage passes to a TAFKAL80ETC concert"};
		item.sellIn = sellIn
		item.quality = initialQuality
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
		Item item = allItems.find {it.name == "Backstage passes to a TAFKAL80ETC concert"};
		item.sellIn = sellIn
		item.quality = initialQuality
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
		Item item = allItems.find {it.name == "Backstage passes to a TAFKAL80ETC concert"};
		item.sellIn = sellIn
		item.quality = initialQuality
		program.setItems([item]);
		when:
		program.updateQuality();
		then:
		0 == item.quality
	}
}
