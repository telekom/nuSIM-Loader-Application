<script>
import Layout from '@layouts/main'

export default {
  page: {
    title: '404',
    meta: [{ name: 'description', content: '404' }],
  },
  components: { Layout },
  props: {
    resource: {
      type: String,
      default: '',
    },
  },
}
</script>

<template>
    <Layout>
        <h1>
            404
            <span v-if="resource">
                {{ resource }}
            </span>
            Not Found
        </h1>
    </Layout>
</template>
