<script>
// Allows stubbing BaseLink in unit tests
const BaseLink = 'BaseLink'

export default {
  // Functional components are stateless, meaning they can't
  // have data, computed properties, etc and they have no
  // `this` context.
  functional: true,
  props: {
    routes: {
      type: Array,
      required: true,
    },
  },
  // Render functions are an alternative to templates
  render(h, { props, $style = {} }) {
    function getRouteTitle(route) {
      return typeof route.title === 'function' ? route.title() : route.title
    }
    function getIcon(route) {
      if (route.img) {
          return <img src={route.img} width={route.imgWidth} height={route.imgHeight} />
      } else if (route.icon) {
        return <span class={'icon icon-' + (typeof route.icon === 'function' ? route.icon() : route.icon)} />
      } else {
        return ''
      }
    }

    // Functional components are the only components allowed
    // to return an array of children, rather than a single
    // root node.
    return props.routes.map(route => (
      <BaseLink tag="li" key={route.name} to={route} exact-active-class={$style.active}>
        <a title={route.tooltip} class={route.img ? 'img' : ''} >
          <span>{getRouteTitle(route)}</span>
          {getIcon(route)}
        </a>
      </BaseLink>
    ))
  },
}
</script>
