import type { CollectionEntry } from "astro:content";

export type SidebarItem = {
  title: string;
  slug: string;
  order?: number;
  children: SidebarItem[];
};

const DEFAULT_ORDER = 1_000_000;

export function entryToRouteSlug(entry: CollectionEntry<"docs">): string {
  const parts = entry.slug.split("/");
  if (parts[parts.length - 1] === "index") {
    parts.pop();
  }
  const joined = parts.join("/");
  return joined || "index";
}

export function routeSlugFromParams(slugParam?: string | string[]): string {
  if (!slugParam) return "index";
  const raw = Array.isArray(slugParam) ? slugParam : slugParam.split("/");
  const joined = raw.filter(Boolean).join("/");
  return joined || "index";
}

export function findEntryByRouteSlug(
  entries: CollectionEntry<"docs">[],
  slugParam?: string | string[]
): CollectionEntry<"docs"> | undefined {
  const target = routeSlugFromParams(slugParam);
  return entries.find((entry) => entryToRouteSlug(entry) === target);
}

export function buildSidebarTree(
  entries: CollectionEntry<"docs">[]
): SidebarItem[] {
  const tree: SidebarItem[] = [];
  const docs = entries.map((entry) => ({
    slug: entryToRouteSlug(entry),
    title:
      entry.data.title ??
      titleFromSlugPart(entryToRouteSlug(entry).split("/").pop() ?? "index"),
    order: entry.data.order ?? DEFAULT_ORDER,
  }));

  for (const doc of docs) {
    insertNode(tree, doc);
  }

  sortTree(tree);
  return tree;
}

function insertNode(
  tree: SidebarItem[],
  doc: { slug: string; title: string; order?: number }
): void {
  const parts = doc.slug === "index" ? ["index"] : doc.slug.split("/");
  let currentLevel = tree;
  const acc: string[] = [];

  for (let i = 0; i < parts.length; i++) {
    const part = parts[i];
    acc.push(part);
    const slugAtLevel = acc.join("/");
    let node = currentLevel.find((item) => item.slug === slugAtLevel);
    if (!node) {
      node = {
        title: titleFromSlugPart(part),
        slug: slugAtLevel,
        order: doc.order ?? DEFAULT_ORDER,
        children: [],
      };
      currentLevel.push(node);
    }

    if (i === parts.length - 1) {
      node.title = doc.title || node.title;
      node.order = doc.order ?? node.order;
    }

    currentLevel = node.children;
  }
}

function sortTree(nodes: SidebarItem[]): void {
  nodes.sort(
    (a, b) =>
      (a.order ?? DEFAULT_ORDER) - (b.order ?? DEFAULT_ORDER) ||
      a.title.localeCompare(b.title)
  );
  nodes.forEach((node) => sortTree(node.children));
}

function titleFromSlugPart(part: string): string {
  if (!part || part === "index") return "Overview";
  return part
    .replace(/[-_]+/g, " ")
    .replace(/\b\w/g, (char) => char.toUpperCase());
}

export function hrefFromSlug(slug: string, base: string): string {
  const normalizedBase = base.endsWith("/") ? base.slice(0, -1) : base;
  const cleanedSlug = slug === "index" ? "" : `/${slug}`;
  return `${normalizedBase}/docs${cleanedSlug}/`;
}
