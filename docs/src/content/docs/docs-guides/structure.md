---
title: "Structure & Routing"
order: 3
---

## Nested routes

Folders define nested routes automatically. For example:

```
src/content/docs/
├─ docs-guides/
│  ├─ index.md            -> /docs/docs-guides/
│  ├─ structure.md        -> /docs/structure/
│  └─ getting-started.md  -> /docs/docs-guides/getting-started/
└─ index.md/              -> /docs/
```

## Sidebar

The sidebar mirrors the folder hierarchy. Entries are sorted by `order`, then by title. Create `index.md` in a folder to control the label of that folder in the navigation.

## Markdown features

Standard Markdown is supported, including code fences:

```ts
function hello(name: string) {
  return `Hello ${name}!`;
}
```
