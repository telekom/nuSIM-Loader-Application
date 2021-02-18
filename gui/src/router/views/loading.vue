<script>
import Layout from '@layouts/main'

export default {
  page: {
    title: 'Loading page...',
    meta: [{ name: 'description', content: 'Loading page...' }],
  },
  components: { Layout },
}
</script>

<template>
    <Layout>
        <transition appear>
            <BaseIcon
                    :class="$style.loadingIcon"
                    name="sync"
                    spin
            />
        </transition>
    </Layout>
</template>
