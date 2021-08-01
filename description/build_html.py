#!/usr/bin/env python3
#-*- coding: utf-8 -*-

import pypandoc
from premailer import transform

def makeStage1():
    stage1 = open("stage1.html", "wb")
    converted = pypandoc.convert_file("description.md",
                                      "html",
                                      format="md",
                                      filters=["pandoc-include"]
    )
    stage1.write(converted.encode("utf-8"))


def main():
    with open("output.html", "wb") as output:
        output.write(
            transform(
                pypandoc.convert_file(
                    "description.md",
                    "html",
                    format="md",
                    filters=["pandoc-include"]
                ),
                allow_loading_external_files=True,
                external_styles=["style.css"],
                remove_classes=False
            )
            .replace("<html><head></head><body>", "")
            .replace("</body></html>", "")
            .encode("utf-8")
        )

if __name__ == "__main__":
    main()
