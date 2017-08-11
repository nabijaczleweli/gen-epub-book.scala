package xyz.nabijaczleweli.gen_epub_book

import java.io.File
import java.text.ParseException

import org.junit.Test

class SettingsTests {
	@Test
	def incorrect() {
		try {
			new Settings(Nil.toArray)
		} catch {
			case _: AssertionError =>
		}

		try {
			new Settings(("" :: Nil).toArray)
		} catch {
			case _: AssertionError =>
		}

		try {
			new Settings(("" :: "" :: "" :: Nil).toArray)
		} catch {
			case _: AssertionError =>
		}

		try {
			new Settings(("" :: "" :: "" :: "" :: Nil).toArray)
		} catch {
			case _: AssertionError =>
		}

		try {
			new Settings(("nonexistant" :: "" :: Nil).toArray)
		} catch {
			case _: ParseException =>
		}
	}

	@Test
	def correct() {
		val s = new Settings(("examples/simple.epupp" :: "out/simple.epub" :: Nil).toArray)
		assert(s.inFile == new File("examples/simple.epupp"))
		assert(s.outFile == new File("out/simple.epub"))
	}
}
