package xyz.nabijaczleweli.gen_epub_book.book

import java.net.URL

import org.junit.Test

class ElementTests {
	@Test
	def `parseLine() -- no element`() {
		assert(Element.parseLine("ImageContent: simple/chapter_image.png").isEmpty)
		assert(Element.parseLine("# Image-Content: simple/chapter_image.png").isEmpty)
		assert(Element.parseLine("# Image-Connt: simple/chapter_image.png").isEmpty)
		assert(Element.parseLine("Old man stabbed the abdomen and yelled 'jackpot!'").isEmpty)
	}

	@Test
	def `parseLine() -- element`() {
		assert(Element.parseLine("Name: Everything we got, in one thing").contains(NameElement("Everything we got, in one thing")))
		assert(Element.parseLine("Content \t: simple/ctnt.html \t\t").contains(ContentElement("simple/ctnt.html")))
		assert(Element.parseLine("String-Content: <strong>SEIZE THE MEANS OF PRODUCTION!</strong").contains(StringContentElement("<strong>SEIZE THE MEANS OF PRODUCTION!</strong")))
		assert(Element.parseLine("Image-Content: simple/chapter_image.png").contains(ImageContentElement("simple/chapter_image.png")))
		assert(Element.parseLine("Network-Image-Content: https://cdn.rawgit.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png").contains(NetworkImageContentElement(new URL("https://cdn.rawgit.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png"))))
		assert(Element.parseLine("Include: with_style/style.css").contains(IncludeElement("with_style/style.css")))
		assert(Element.parseLine("Network-Include: http://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.css").contains(NetworkIncludeElement(new URL("http://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.css"))))
		assert(Element.parseLine("Network-Cover: http://i.imgur.com/ViQ2WED.jpg").contains(NetworkCoverElement(new URL("http://i.imgur.com/ViQ2WED.jpg"))))
		assert(Element.parseLine("Author: nabijaczleweli").contains(AuthorElement("nabijaczleweli")))
		assert(Element.parseLine("Language: en-GB").contains(LanguageElement("en-GB")))
	}
}
