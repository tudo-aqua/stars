---
title: "Getting Started"
order: 2
---

## How to add pages

1. Create a new Markdown or MDX file inside `src/content/docs`.
2. Use folders to create nested routes (for example `stars/setup.md` becomes `/docs/stars/setup/`).
3. Add optional frontmatter fields like `title`, `description`, and `order` to control labels and sorting.

## Frontmatter reference

```
---
title: "Page Title"
order: 10
description: "Optional summary shown in listings."
---
```

## Linking between pages

Use relative links like `[STARS](./stars/setup/)` or absolute links such as `/docs/stars/setup/`.
