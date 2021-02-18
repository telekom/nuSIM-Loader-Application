export default function pathPrefix() {
  let pathname = ''
  if (typeof window !== 'undefined') {
    const bases = document.getElementsByTagName('base')
    if (bases.length > 0) {
      const baseHref = bases[0].href
      pathname = new URL(baseHref).pathname
    }
  }
  return pathname
}
