<script>
    import NavBar from '@components/nav-bar'
    import InfoDialog from '@components/InfoDialog'
    import ProvisioningDialog from '@components/ProvisioningDialog'
    import RequestProfileDialog from '@components/RequestProfileDialog'
    import RetrieveProfileDialog from '@components/RetrieveProfileDialog'
    import QueryProfileStockDialog from '@components/QueryProfileStockDialog'
    import QueryProfileStockResultDialog from '@components/QueryProfileStockResultDialog'
    import YesNoDialog from '@components/YesNoDialog'
    import axios from 'axios'
    import store from '@state/store'
    import connectedIcon from '@src/assets/icons/link-solid.svg'
    import disconnectedIcon from '@src/assets/icons/unlink-solid.svg'
    import nuSIMLogo from '@src/assets/images/nuSIM_Label_1C_3C_n.png'
    import path from 'path'
    import pathPrefix from '@src/pathPrefix'
    import instantFormat from '@utils/instantFormat'

    export default {
  components: {
    InfoDialog,
    QueryProfileStockDialog,
    NavBar,
    ProvisioningDialog,
    RequestProfileDialog,
    RetrieveProfileDialog,
    QueryProfileStockResultDialog,
    YesNoDialog,
  },
  store: store,
  data() {
    return {
      onImage: connectedIcon,
      offImage: disconnectedIcon,
      nuSIMLogo: nuSIMLogo,
      demoModeFeature: false,
    }
  },
  computed: {
    started: function() {
      return this.$store.state.nusimapp.serverstate.provisioningInProgress
    },
    iconClass: function() {
      return this.$store.state.nusimapp.serverstate.websocket
    },
    iconTooltip: function() {
      return this.$store.state.nusimapp.serverstate.websocketError
    },
  },
  mounted() {
    axios
      .get(path.join(pathPrefix(), '/api/v1/configuration'))
      .then(response => {
        this.demoModeFeature = response.data.provisioning.demoModeFeature
      })
      .catch(error => {
        this.$refs.InfoDialog.error('Load Configuration', 'Could not get configuration from server: ' + error.message)
        this.demoModeFeature = false
      })
  },
  methods: {
    infoDialog(title, message) {
      this.$refs.InfoDialog.info(title, message)
        .then(ignored => {})
        .catch(ignored => {})
    },
    errorDialog(title, message) {
      this.$refs.InfoDialog.error(title, message)
        .then(ignored => {})
        .catch(ignored => {})
    },
      toggleProvisioning() {
          const action = this.started ? 'stop' : 'start'

          axios.get(path.join(pathPrefix(), '/api/v1/configuration'))
              .then(response => {
                  const mode = response.data.provisioning.mode

                  let promise = null
                  if (!this.started) {
                      promise = this.$refs.ProvisioningDialog.open({mode: mode})
                  } else {
                      promise = Promise.resolve({})
                  }

                  promise
                      .then(request => {
                          axios
                              .put(path.join(pathPrefix(), '/api/v1/provisioning/status'), {status: !this.started, ...request})
                              .then(response => {
                                  this.infoDialog('Profile Loading', action + ' of Provisioning triggered successfully.')
                              })
                              .catch(error => {
                                  this.errorDialog('Profile Loading', 'Could not ' + action + ' provisioning: ' + error.message)
                              })
                      })
                      .catch(cancelled => {
                      })
              })
              .catch(error => {
                  this.errorDialog('Profile Loading', 'Could not retrieve provisioning mode from server: ' + error.message)
              })
      },
      loadKPubDP(overwrite) {
          if (this.demoModeFeature) {
              return
          }
          axios
              .post(path.join(pathPrefix(), '/api/v1/provisioning/loadKPubDP'),{overwrite: overwrite})
              .then(response => {
                  console.log("response: ")
                  console.log(response)
              if (response.data === "EXISTS") { // confirm overwrite
                  this.$refs.YesNoDialog.open('DP Public Key Retrieval', 'Would you like to overwrite existing DP Public Key information?')
                      .then(ok => {
                          this.loadKPubDP(true)
                      })
              } else {
                  this.$refs.InfoDialog.info('DP Public Key Retrieval', 'DP Public Key saved successfully into configuration')
              }
              })
              .catch(error => {
                  this.errorDialog('DP Public Key Retrieval', 'Could not perform action: ' + error.response.data)
              })
      },
    loadCertificates() {
        if (this.demoModeFeature) {
            return;
        }
      axios
        .get(path.join(pathPrefix(), '/api/v1/provisioning/loadCertificates'))
        .then(response => {
          let d = response.data
          if (d.total === 0) {
            this.errorDialog('Certificate Retrieval', 'There are currently no EIDs in database. Use import first.')
          } else if (d.success === 0 && d.error === 0) {
            this.errorDialog('Certificate Retrieval', 'No Certificates loaded - there are no new EIDs without certificate data in database currently.')
          } else if (d.success === 0) {
            this.errorDialog('Certificate Retrieval', `Error retrieving certificates. Of ${d.processed} EIDs, ${d.success} have been handled successfully and ${d.error} errors occurred.`)
          } else {
            this.infoDialog('Certificate Retrieval', `Finished retrieving certificates. Of ${d.processed} EIDs, ${d.success} have been handled successfully and ${d.error} errors occurred.`)
          }
        })
        .catch(error => {
          this.errorDialog('Certificate Retrieval', 'Could not perform action: ' + error.response.data)
        })
    },
    requestProfiles() {
        if (this.demoModeFeature) {
            return;
        }
      this.$refs.RequestProfileDialog.open()
        .then(fulfilled => {
          axios
            .post(path.join(pathPrefix(), '/api/v1/provisioning/requestProfiles'), fulfilled)
            .then(response => {
              this.infoDialog(
                'Bulk Profile Request',
                'Requested ' +
                  response.data.requested +
                  ' Profile' +
                  (response.data.requested !== 1 ? 's' : '') +
                  '. ' +
                  'ReferenceId ' +
                  response.data.referenceId +
                  ' can be retrieved at ' +
                  instantFormat(response.data.retrieveTime)
              )
            })
            .catch(error => {
              this.errorDialog('Bulk Profile Request', 'Could not perform action: ' + error.response.data)
            })
        })
        .catch(cancelled => {})
    },
    retrieveProfiles() {
        if (this.demoModeFeature) {
            return;
        }
      axios
        .get(path.join(pathPrefix(), '/api/v1/provisioning/referenceIds'))
        .then(response => {
          const ids = response.data.entries

          if (ids.length === 0) {
            this.errorDialog('Profile Retrieval', 'No open referenceIds found. Request Profiles first')
          } else {
            this.$refs.RetrieveProfileDialog.open(ids)
              .then(request => {
                axios
                  .post(path.join(pathPrefix(), '/api/v1/provisioning/retrieveProfiles'), request)
                  .then(response => {
                    const r = response.data
                    if (r.errorCode === '405') {
                      this.$refs.YesNoDialog.open('ReferenceID is invalid', 'Would you like to delete invalid referenceId ' + request.referenceId + '?')
                        .then(ok => {
                          axios
                            .delete(path.join(pathPrefix(), '/api/v1/provisioning/referenceId/' + request.referenceId))
                            .then(ignored => {
                              this.infoDialog('Profile Retrieval', 'ReferenceId deleted successfully')
                            })
                            .catch(error => {
                              this.errorDialog('Profile Retrieval', 'Could not perform action: ' + error.response.data)
                            })
                        })
                        .catch(ignored => {})
                    } else {
                      if (r.retrievedTotal === 0) {
                        this.infoDialog('Profile Retrieval', 'No profiles retrieved')
                      } else {
                        this.infoDialog(
                          'Profile Retrieval',
                          'Retrieved ' +
                            r.retrievedTotal +
                            ' Profiles, ' +
                            (r.retrievedTotal - r.retrievedGood) +
                            ' errors, ' +
                            r.addedLocally +
                            ' matched with EIDs in local database'
                        )
                      }
                    }
                  })
                  .catch(error => {
                    this.errorDialog('Profile Retrieval', 'Could not perform action: ' + error.response.data)
                  })
              })
              .catch(cancelled => {})
          }
        })
        .catch(error => {
          this.errorDialog('Profile Retrieval', 'Could not perform action: ' + error.response.data)
        })
    },
    queryProfileStock() {
        if (this.demoModeFeature) {
            return;
        }
      this.$refs.QueryProfileStockDialog.open()
        .then(request => {
          axios
            .post(path.join(pathPrefix(), '/api/v1/provisioning/queryProfileStock'), request)
            .then(response => {
              const list = response.data.stockList

              this.$refs.QueryProfileStockResultDialog.open(list)
                .then(ignore => {})
                .catch(ignore => {})
            })
            .catch(error => {
              this.errorDialog('Profile Stock Query', 'Could not perform action: ' + error.response.data)
            })
        })
        .catch(cancelled => {})
    },
  },
}
</script>

<template>
    <div class="maxheight">
        <provisioning-dialog ref="ProvisioningDialog"/>
        <request-profile-dialog ref="RequestProfileDialog"/>
        <retrieve-profile-dialog ref="RetrieveProfileDialog"/>
        <query-profile-stock-dialog ref="QueryProfileStockDialog"/>
        <query-profile-stock-result-dialog ref="QueryProfileStockResultDialog"/>
        <info-dialog ref="InfoDialog"/>
        <yes-no-dialog ref="YesNoDialog"/>
        <div id="mainContent">
            <header class="brand-header force-unfixed">
                <div class="brandbar">
                    <div class="container-fixed">
                        <div class="col-l-12 col-m-12 col-s-12 container-fixed horizontalFlex">
                            <div class="row horizontalFlex">
                                <div class="row">
                                    <div class="col-l-4 col-m-4 col-s-4">
                                        <div class="brand-logo">
                                            <img
                                                    alt="Telekom Logo"
                                                    src="assets/deutsche-telekom-logo.svg">
                                            <span class="sr-only">Telekom Logo</span>
                                        </div>
                                    </div>
                                    <div class="col-l-4 col-m-4 col-s-4">
                                        <div class="brand-logo" style="text-align: center; width: 100%;">
                                            <img
                                                    :src="nuSIMLogo"
                                                    width="105" height="35"
                                                    alt="nuSIM Logo">
                                            <span class="sr-only">nuSIM Logo</span>
                                        </div>
                                    </div>
                                    <div class="col-l-4 col-m-4 col-s-4">
                                        <span class="tooltip">
                                            <img
                                                    :class="iconClass || 'icon-invisible'"
                                                    :src="onImage"
                                                    width="24px">
                                            <img
                                                    :class="iconClass && 'icon-invisible'"
                                                    :src="offImage"
                                                    width="24px">
                                            <span class="tooltiptext">{{ iconTooltip }}</span>
                                        </span>
                                        <div class="brand-claim">
                                            Life is for Sharing.
                                            <span class="sr-only">Life is for Sharing.</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <nav-bar>
                    <li>
                        <a :class="{ disabled: demoModeFeature }"
                           title="Retrieve Public Key from DP"
                           @click="loadKPubDP(false)">
                            <span>Get KpubDP</span>
                        </a>
                    </li>
                    <li>
                        <a :class="{ disabled: demoModeFeature }"
                           title="Retrieve certificates for EIDs in local DBs"
                           @click="loadCertificates">
                            <span>Get Certs</span>
                        </a>
                    </li>
                    <li>
                        <a :class="{ disabled: demoModeFeature }"
                           title="Request profiles for Bulk Mode Provisioning"
                           @click="requestProfiles">
                            <span>Request Profiles</span>
                        </a>
                    </li>
                    <li>
                        <a :class="{ disabled: demoModeFeature }"
                           title="Retrieve previously requested profiles"
                           @click="retrieveProfiles">
                            <span>Retrieve Profiles</span>
                        </a>
                    </li>
                    <li>
                        <a :class="{ disabled: demoModeFeature }"
                           title="Query profile stock from DP"
                           @click="queryProfileStock">
                            <span>Stock Information</span>
                        </a>
                    </li>
                    <li>
                        <a :title="started ? 'STOP running provisioning' : 'START provisioning'"
                           @click="toggleProvisioning">
                            <span>{{ started ? "STOP" : "START" }}
                            </span>
                        </a>
                    </li>
                </nav-bar>
            </header>

            <div class="col-l-10 col-m-12 col-s-12 container-fixed horizontalFlex">
                <div
                        id="container"
                        class="row horizontalFlex">

                    <slot/>

                </div>
            </div>
            <footer/>
        </div>
    </div>
</template>
