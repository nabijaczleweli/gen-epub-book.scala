# The MIT License (MIT)

# Copyright (c) 2017 nabijaczleweli

# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:

# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


ifeq "$(OS)" "Windows_NT"
	ECHO := /bin/echo -e
else
	ECHO := echo
endif

GRADLE := gradle
CALIBRE_CONVERT := ebook-convert
OUTDIR := out/

BOOK_SOURCES := $(sort $(wildcard examples/*.epupp examples/**/*.epupp examples/**/**/*.epupp examples/**/**/**/*.epupp))
SOURCES := $(sort $(wildcard src/*.scala src/**/*.scala src/**/**/*.scala src/**/**/**/*.scala src/**/**/**/**/*.scala src/**/**/**/**/**/*.scala src/**/**/**/**/**/**/*.scala src/**/**/**/**/**/**/**/*.scala))

.PHONY : all examples test


all : examples test

clean :
	rm -rf $(OUTDIR)

examples : $(SOURCES) $(patsubst examples/%.epupp,$(OUTDIR)%.epub,$(BOOK_SOURCES)) $(patsubst examples/%.epupp,$(OUTDIR)%.mobi,$(BOOK_SOURCES))
test : $(SOURCES)
	$(GRADLE) test

$(OUTDIR)%.mobi : $(OUTDIR)%.epub
	$(CALIBRE_CONVERT) "$^" "$@"

$(OUTDIR)%.epub : examples/%.epupp $(SOURCES)
	@mkdir -p $(dir $@)
	$(GRADLE) run -PappArgs="['$<', '$@', '-Irel=examples/relative_path_fuckery/relative/path']"
