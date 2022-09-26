#!/usr/bin/env python3
#-*- coding: utf-8 -*-

import pypandoc
from pathlib import Path

def main():
    Path("out/debug").mkdir(parents=True, exist_ok=True)

    html = pypandoc.convert_file(
        "release_notes.md",
        "md",
        format="md",
        extra_args=["--wrap=preserve"],
        filters=["pandoc-include"]
    )

    with open("out/pandoc-release_notes.md", "wb") as f:
        f.write(html.encode("utf-8"))


if __name__ == "__main__":
    main()
