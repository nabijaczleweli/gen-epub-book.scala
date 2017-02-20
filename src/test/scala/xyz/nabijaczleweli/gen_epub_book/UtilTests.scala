package xyz.nabijaczleweli.gen_epub_book

import java.io.{File, PrintWriter}
import java.net.URL

import org.junit.Test
import xyz.nabijaczleweli.gen_epub_book.Util

import scala.io.Source

class UtilTests {
	@Test
	def pathId() {
		assert(Util.pathId("simple/chapter_image.png") == "simple-chapter_image-png")
		assert(Util.pathId("simple/ctnt.html") == "simple-ctnt-html")
		assert(Util.pathId("../cover.png") == "cover-png")
		assert(Util.pathId("relative/path/../green_ass_dog.html") == "relative-path-green_ass_dog-html")
		assert(Util.pathId("./../relative_path_fuckery\\relative/../relative/path\\../../relative/path/dead_santa.html") ==
			"relative_path_fuckery-relative-relative-path-relative-path-dead_santa-html")
		assert(Util.pathId("../cover") == "cover")
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
		assert(Util.urlId(new URL("http://i.imgur.com/ViQ2WED.jpg")) == "ViQ2WED")
		assert(Util.urlId(new URL("https://cdn.rawgit.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png")) == "slim_shady")
		assert(Util.urlId(new URL("https://img09.deviantart.net/e6c8/i/2015/138/8/0/the_pursuer_by_artsed-d7lbiua.jpg")) == "the_pursuer_by_artsed-d7lbiua")
		assert(Util.urlId(new URL("https://i.imgur.com/")) == "")
		assert(Util.urlId(new URL("https://i.imgur.com/.png")) == "")
	}

	@Test
	def urlFilename() {
		assert(Util.urlFilename(new URL("http://i.imgur.com/ViQ2WED.jpg")) == "ViQ2WED.jpg")
		assert(Util.urlFilename(new URL("https://cdn.rawgit.com/nabijaczleweli/nabijaczleweli.github.io/dev/src/writing_prompts/slim_shady.png")) == "slim_shady.png")
		assert(Util.urlFilename(new URL("https://img09.deviantart.net/e6c8/i/2015/138/8/0/the_pursuer_by_artsed-d7lbiua.jpg")) == "the_pursuer_by_artsed-d7lbiua.jpg")
		assert(Util.urlFilename(new URL("https://i.imgur.com/")) == "")
		assert(Util.urlFilename(new URL("https://i.imgur.com/.png")) == ".png")
	}

	@Test
	def `findTitle() -- no title` {
		assert(Util.findTitle(new File("src/main/scala/xyz/nabijaczleweli/gen_epub_book/Assets.scala")) == None)
		assert(Util.findTitle(new File("LICENSE")) == None)
	}

	@Test
	def `findTitle() -- close` {
		val f = File.createTempFile("gen-epub-book", ".scala")
		val w = new PrintWriter(f)
		w write "<!-- ePub title: \"\"\" -->\n"
		w write "<!-- ePub title: \"No right quote -->\n"
		w write "<!-- ePub title: No right quote\" -->\n"
		w write "<!-- ePub title: \"No right quote\"\n"
		w write "ePub title: \"No right quote\" -->\n"
		w.close()
		assert(Util.findTitle(f) == None)
	}

	@Test
	def `findTitle() -- with title` {
		assert(Util.findTitle(new File("examples/simple/ctnt.html")) == Some("Chapter 1, Where everything's still going mostly right"));
	}
}
