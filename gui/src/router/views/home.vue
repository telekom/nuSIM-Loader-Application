<script>
import appConfig from '@src/app.config'
import Layout from '@layouts/main'
import store from '@state/store'
import Vue from 'vue'

export default {
  page: {
    title: 'Logging',
    meta: [{ name: 'description', content: appConfig.description }],
  },
  components: { Layout },
  store: store,
  computed: {
    loglines: function() {
      return this.$store.state.nusimapp.loglines
    },
    loglevel: function() {
      return this.$store.state.nusimapp.loglevel
    },
    followLogs: function() {
      return this.$store.state.nusimapp.followLogs
    },
  },
  mounted: function() {
    this.$store.subscribe((mutation, state) => {
      if (
        (mutation.type === 'nusimapp/SET_FOLLOWLOGS' || mutation.type === 'nusimapp/SET_LOGLINES' || mutation.type === 'nusimapp/SET_LOGLEVEL') &&
        state.nusimapp.followLogs
      ) {
        Vue.nextTick(this.scrollDown)
      }
    })
    if (this.$store.state.nusimapp.followLogs) {
      Vue.nextTick(this.scrollDown)
    }
  },
  methods: {
    scrollDown() {
      Vue.nextTick(() => {
        const myDiv = this.$el.querySelector('#scrollport')
        myDiv.scrollTop = myDiv.scrollHeight
      })
    },
    rowClass(loglevel) {
      return loglevel
    },
    showLog(lineLevel, loglevel) {
      if (loglevel === 'DEBUG') {
        return true
      }
      if (loglevel === lineLevel) {
        return true
      }
      if (loglevel === 'INFO') {
        return lineLevel === 'WARN' || lineLevel === 'ERROR'
      }
      if (loglevel === 'WARN') {
        return lineLevel === 'ERROR'
      }
      return false
    },
    setLoglevel(e) {
      this.$store.commit('nusimapp/SET_LOGLEVEL', e.target.value)
    },
    setFollowLogs(e) {
      this.$store.commit('nusimapp/SET_FOLLOWLOGS', e)
    },
  },
}
</script>

<template>
    <Layout>
        <div class="row">
            <div class="col-l-2 col-m-4 col-s-12">
                <div class="form-input-set">
                    <label
                            for="selectbox"
                            title="Loglevel">Loglevel</label>
                    <select
                            id="selectbox"
                            :value="loglevel"
                            name="select"
                            class="form-select"
                            @input="setLoglevel">
                        <option value="DEBUG">DEBUG</option>
                        <option value="INFO">INFO</option>
                        <option value="WARN">WARNINGS</option>
                        <option value="ERROR">ERROR</option>
                    </select>
                </div>
            </div>
            <div class="col-l-8 col-m-4 col-s-12"/>
            <div class="col-l-2 col-m-4 col-s-12">
                <div class="form-input-set">
                    <v-checkbox
                            :input-value="followLogs"
                            label="FollowLogs"
                            hide-details
                            @change="setFollowLogs"
                    />
                </div>
            </div>
        </div>
        <div class="row horizontalFlex">
            <div class="col-l-12 col-m-12 col-s-12 horizontalFlex">
                <div
                        id="scrollport"
                        class="logtable horizontalFlex">
                    <table class="table-small loggingtable">
                        <thead>
                            <th>Date</th>
                            <th>Log Level</th>
                            <th>Message</th>
                        </thead>
                        <tbody>
                            <tr
                                    v-for="(e, i) in loglines"
                                    v-if="showLog(e.loglevel, loglevel)"
                                    :key="i"
                                    :class="rowClass(e.loglevel)"
                            >
                                <td>{{ new Date(e.t).toLocaleString() }}</td>
                                <td>{{ e.loglevel }}</td>
                                <td>{{ e.message }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </Layout>
</template>
