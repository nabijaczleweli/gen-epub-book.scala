package xyz.nabijaczleweli.gen_epub_book

import java.io.File

import org.junit.Test
import xyz.nabijaczleweli.gen_epub_book.feature.IncludeDirectory

class SettingsTests {
	@Test
	def correct() {
		val s = new Settings(Array("-Irpf=examples/relative_path_fuckery/relative/path", "examples/simple.epupp", "-Isrc", "out/simple.epub", "-Iw=gradle/wrapper"))
		assert(s.inFile == new File("examples/simple.epupp"))
		assert(s.outFile == new File("out/simple.epub"))
		assert(s.includeDirs equals
			new IncludeDirectory(None, new File("examples")) ::
			new IncludeDirectory(Some("rpf"), new File("examples/relative_path_fuckery/relative/path")) ::
			new IncludeDirectory(None, new File("src")) ::
			new IncludeDirectory(Some("w"),	new File("gradle/wrapper")) :: Nil)
	}
}
