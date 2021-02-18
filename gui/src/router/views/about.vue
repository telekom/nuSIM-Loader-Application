<script>
import appConfig from '@src/app.config'
import Layout from '@layouts/main'

export default {
  page: {
    title: 'About',
    meta: [{ name: 'description', content: appConfig.description }],
  },
  components: { Layout },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},
  created() {},
  methods: {},
}
</script>

<template>
    <Layout>
        <v-container>
            <v-layout align-center>
                <div class="col-l-12 col-m-12 col-s-12">
                    <h3 class="display-3">nuSIM Integrated SIM LoaderApp</h3>
                    <span class="subheading">A Proof-of-Concept application allowing vendor specific provisioning of nuSIMs</span>
                    <v-divider class="my-3"/>
                    <div class="title mb-3">This application is developed by</div>
                    <div class="col-l-6 col-m-6 col-s-6">
                        <div>
                            SCOOP Software GmbH<br>
                            Gut Maarhausen<br>
                            Eiler Straße 3P<br>
                            D - 51107 Köln<br>
                            <a href="mailto:info@scoop-software.de">info@scoop-software.de</a><br>
                            +49 221 801916-0
                        </div>
                        <v-divider class="my-3"/>
                        <a
                                href="https://www.scoop-software.de/en/"
                                target="_blank">
                            <button
                                    type="button"
                                    class="btn btn-default">Visit us!
                            </button>
                        </a>
                    </div>
                    <div
                            class="col-l-6 col-m-6 col-s-6"
                            align="center">
                        <div>
                            <img src="../../assets/images/scoop_logo_rgb_72dpi.png">
                        </div>
                    </div>
                </div>
            </v-layout>
        </v-container>
    </Layout>
</template>
