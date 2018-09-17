package xyz.nabijaczleweli.gen_epub_book

import java.io.{File, PrintWriter}
import java.net.URL

import org.junit.Test

class UtilTests {
	@Test
	def pathId() {
		assert(Util.pathID("simple/chapter_image.png") == "simple-chapter_image_png")
		assert(Util.pathID("simple/ctnt.html") == "simple-ctnt_html")
		assert(Util.pathID("../cover.png") == "cover_png")
		assert(Util.pathID("relative/path/../green_ass_dog.html") == "relative-path-green_ass_dog_html")
		assert(Util.pathID("./../relative_path_fuckery\\relative/../relative/path\\../../relative/path/dead_santa.html") ==
			"relative_path_fuckery-relative-relative-path-relative-path-dead_santa_html")
		assert(Util.pathID("../cover") == "cover")
	}

	@Test
	def pathFilename() {
		assert(Util.pathFilename("simple/chapter_image.png") == "simple-chapter_image.png")
		assert(Util.pathFilename("simple/ctnt.html") == "simple-ctnt.html")
		assert(Util.pathFilename("../cover.png") == "cover.png")
		assert(Util.pathFilename("relative/path/../green_ass_dog.html") == "relative-path-green_ass_dog.html")
		assert(Util.pathFilename("./../relative_path_fuckery\\relative/../relative/path\\../../relative/path/dead_santa.html") ==
			"relative_path_fuckery-relative-relative-path-relative-path-dead_santa.html")
		assert(Util.pathFilename("../cover") == "cover")
	}

	@Test
	def urlId() {
		assert(Util.urlId(new URL("http://i.imgur.com/ViQ2WED.jpg")) == "ViQ2WED_jpg")
		assert(Util.urlId(new URL("https://rawcdn.githack.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png")) == "slim_shady_png")
		assert(Util.urlId(new URL("https://img09.deviantart.net/e6c8/i/2015/138/8/0/the_pursuer_by_artsed-d7lbiua.jpg")) == "the_pursuer_by_artsed-d7lbiua_jpg")
		assert(Util.urlId(new URL("https://i.imgur.com/")) == "")
		assert(Util.urlId(new URL("https://i.imgur.com/.png")) == "_png")
	}

	@Test
	def urlFilename() {
		assert(Util.urlFilename(new URL("http://i.imgur.com/ViQ2WED.jpg")) == "ViQ2WED.jpg")
		assert(Util.urlFilename(new URL("https://rawcdn.githack.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png")) == "slim_shady.png")
		assert(Util.urlFilename(new URL("https://img09.deviantart.net/e6c8/i/2015/138/8/0/the_pursuer_by_artsed-d7lbiua.jpg")) == "the_pursuer_by_artsed-d7lbiua.jpg")
		assert(Util.urlFilename(new URL("https://i.imgur.com/")) == "")
		assert(Util.urlFilename(new URL("https://i.imgur.com/.png")) == ".png")
	}

	@Test
	def `findTitle() -- no title`() {
		assert(Util.findTitle(new File("src/main/scala/xyz/nabijaczleweli/gen_epub_book/Assets.scala")).isEmpty)
		assert(Util.findTitle(new File("LICENSE")).isEmpty)
	}

	@Test
	def `findTitle() -- close`() {
		val f = File.createTempFile("gen-epub-book", ".scala")
		val w = new PrintWriter(f)
		w write "<!-- ePub title: \"\"\" -->\n"
		w write "<!-- ePub title: \"No right quote -->\n"
		w write "<!-- ePub title: No right quote\" -->\n"
		w write "<!-- ePub title: \"No right quote\"\n"
		w write "ePub title: \"No right quote\" -->\n"
		w.close()
		assert(Util.findTitle(f).isEmpty)
	}

	@Test
	def `findTitle() -- with title`() {
		assert(Util.findTitle(new File("examples/simple/ctnt.html")).contains("Chapter 1, Where everything's still going mostly right"))
	}

	@Test
	def guessMimeType() {
		assert(Util.guessMimeType("examples/img.png") == "image/png")
		assert(Util.guessMimeType("examples/img.jpg") == "image/jpeg")
		assert(Util.guessMimeType("examples/index.js") == "application/javascript")
		assert(Util.guessMimeType("examples/style.css") == "text/css")
		assert(Util.guessMimeType("ctnt.html") == "application/xhtml+xml")
	}
}
