package xyz.nabijaczleweli.gen_epub_book

import xyz.nabijaczleweli.gen_epub_book.book.{Book, load => loadElements}

object Main extends App {
	val settings = new Settings(args)
	val bookElements = loadElements(settings.inFile, settings.separator)
	val book = new Book(settings.includeDirs, bookElements)
	book.write(settings.outFile)
}
