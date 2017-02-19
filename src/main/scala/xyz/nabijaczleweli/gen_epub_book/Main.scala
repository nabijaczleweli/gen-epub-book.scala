package xyz.nabijaczleweli.gen_epub_book

object Main extends App {
	val settings = new Settings(args)
	println(settings.inFile)
	println(settings.outFile)
	println(settings.relativeDirectoryRoot)
}
