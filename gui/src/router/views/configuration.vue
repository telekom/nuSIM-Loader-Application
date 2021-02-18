<script>
import appConfig from '@src/app.config'
import Layout from '@layouts/main'
import ConfigurationLine from '@src/components/configurationLine'
import InfoDialog from '@components/InfoDialog'
import axios from 'axios'
import SCHEMA from '@src/schema.json'
import QUICKSCHEMA from '@src/quickschema.json'
import path from 'path'
import pathPrefix from '@src/pathPrefix'

export default {
  page: {
    title: 'Configuration',
    meta: [{ name: 'description', content: appConfig.description }],
  },
  components: { Layout, ConfigurationLine, InfoDialog },
  data() {
    return {
      configuration: null,
      shadowConfiguration: null,
      configurationDef: {
        NusimSimHTTPAdapter: {
          name: 'nuSIM HTTP Adapter',
        },
      },
      schema: SCHEMA,
      quickschema: QUICKSCHEMA,
      hotChangeKeys: ['provisioning.mode', 'provisioning.certificateSource', 'if1.certCM'],
      o: [true],
    }
  },
  computed: {
    hotChangeAllowed: function() {
      return this.visit(this.shadowConfiguration, this.configuration)
    },
  },
  watch: {},
  mounted() {
    axios
      .get(path.join(pathPrefix(), '/api/v1/configuration'))
      .then(response => {
        this.configuration = response.data
        this.shadowConfiguration = JSON.parse(JSON.stringify(response.data))
      })
      .catch(error => {
        this.$refs.InfoDialog.error('Load Configuration', 'Could not load Configuration: ' + error.message)
        this.configuration = null
        this.shadowConfiguration = null
      })
  },
  created() {},
  methods: {
    visit(s, o, root) {
      for (let [key, value] of Object.entries(s)) {
        const newPath = root ? root + '.' + key : key
        if (typeof value === 'object') {
          const v = this.visit(value, o[key], newPath)
          if (v === false) {
            return false
          }
        } else {
          if (value !== o[key]) {
            if (this.hotChangeKeys.findIndex(a => a === newPath) === -1) {
              return false
            }
          }
        }
      }
      return true
    },
    setConfiguration() {
      const hotChange = this.visit(this.shadowConfiguration, this.configuration)
      axios
        .put(path.join(pathPrefix(), '/api/v1/configuration'), this.configuration)
        .then(response => {
          if (hotChange) {
            this.$refs.InfoDialog.info('Save Configuration', 'Configuration saved successfully and is in effect')
          } else {
            this.$refs.InfoDialog.error('Save Configuration', 'Configuration saved successfully. Restart needed for configuration to become effective!')
          }
          this.shadowConfiguration = JSON.parse(JSON.stringify(this.configuration))
        })
        .catch(error => {
          this.$refs.InfoDialog.error('Save Configuration', 'Could not save Configuration: ' + error.message)
        })
    },
      visible: function(data) {
          if (data === 'certificateSource' && this.configuration['provisioning']['mode'] === 'bulk') {
              return false;
          }
          return true
      },
  },
}
</script>

<template>
    <Layout>
        <info-dialog ref="InfoDialog"/>
        <div
                v-if="configuration"
                id="scrollport"
                class="col-l-12 col-m-12 col-s-12 horizontalFlex">
            <v-expansion-panel expand>
                <v-expansion-panel-content :value="true">
                    <div slot="header">
                        <v-icon color="blue">info</v-icon>
                        Provisioning
                    </div>
                    <v-card>
                        <v-card-text class="grey lighten-2">
                            <v-form>
                                <template v-for="topLevel in quickschema">
                                    <template v-for="l1 in topLevel.meta.children">
                                        <configuration-line
                                                v-if="visible(l1.name)"
                                                :schema="l1"
                                                :key="l1.name"
                                                v-model="configuration[topLevel.name][l1.name]"
                                                class="subsection-content"
                                        />
                                    </template>
                                </template>
                            </v-form>
                        </v-card-text>
                    </v-card>
                </v-expansion-panel-content>
                <v-expansion-panel-content
                        v-for="topLevel in schema"
                        :key="topLevel.name">
                    <div slot="header">{{ topLevel.meta.displayName }}</div>
                    <v-card>
                        <v-card-text class="grey lighten-3">
                            <v-form>
                                <div class="subsection">{{ topLevel.meta.generalName || 'General' }}</div>
                                <template v-for="l1 in topLevel.meta.children">
                                    <configuration-line
                                            v-if="! l1.meta.children"
                                            :schema="l1"
                                            :key="l1.name"
                                            v-model="configuration[topLevel.name][l1.name]"
                                            class="subsection-content"
                                    />
                                    <template v-if="l1.meta.children">
                                        <v-divider :key="'div'+l1.name"/>
                                        <div
                                                :key="'sub'+l1.name"
                                                class="subsection">{{ l1.meta.displayName }}
                                        </div>
                                        <configuration-line
                                                v-for="l2 in l1.meta.children"
                                                v-if="visible(topLevel.name+'.'+l1.name+'.'+l2.name)"
                                                :schema="l2"
                                                v-model="configuration[topLevel.name][l1.name][l2.name]"
                                                :key="topLevel.name+'.'+l1.name+'.'+l2.name"
                                                class="subsection-content"
                                        />
                                    </template>
                                </template>

                            </v-form>
                        </v-card-text>
                    </v-card>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </div>
        <button
                type="button"
                class="btn btn-default"
                @click="setConfiguration"
        >Save
        </button>
    </Layout>
</template>
