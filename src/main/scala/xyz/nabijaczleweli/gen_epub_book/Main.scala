package xyz.nabijaczleweli.gen_epub_book

object Main extends App {
	val settings = new Settings(args)
	val bookElements = book.load(settings.inFile)
	println(bookElements.length)
	for(el <- bookElements.iterator map (e => e.getClass))
		println(el)
}
