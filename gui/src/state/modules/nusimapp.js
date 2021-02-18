import store from '@state/store'
import path from 'path'
import pathPrefix from '@src/pathPrefix'

startWebsocket(path.join(pathPrefix(), '/api/v1/server/state'), event => {
  let eventData = JSON.parse(event.data)
  if (eventData.provisioningInProgress !== state.serverstate.provisioningInProgress) {
    store.commit('nusimapp/SET_SERVERSTATE', eventData)
  }
})

const LOG_BUFFER_SIZE = 30000
startWebsocket(path.join(pathPrefix(), '/api/v1/logging/live'), event => {
  let eventData = JSON.parse(event.data)
  if (eventData.length > 0) {
    store.commit('nusimapp/SET_LOGLINES', eventData)
  }
})

export const state = {
  serverstate: {
    provisioningInProgress: false,
    websocket: true,
    websocketError: '',
  },
  loglines: [],
  loglevel: 'INFO',
  followLogs: true,
}

export const mutations = {
  SET_SERVERSTATE(state, event) {
    state.serverstate = { ...state.serverstate, ...event }
    state.serverstate.websocketError = 'connected to server'
  },
  SET_WEBSOCKET_ERROR(state, event) {
    state.serverstate.websocket = false
    state.serverstate.websocketError = event
  },
  SET_WEBSOCKET_CLOSE(state, event) {
    state.serverstate.websocket = false
    state.serverstate.websocketError = event
  },
  SET_WEBSOCKET_OPEN(state, event) {
    state.serverstate.websocket = true
    state.serverstate.websocketError = 'connected to server'
  },
  SET_LOGLINES(state, event) {
    state.loglines.push(...event)
    while (state.loglines.length > LOG_BUFFER_SIZE) {
      state.loglines.shift()
    }
  },
  SET_LOGLEVEL(state, event) {
    state.loglevel = event
  },
  SET_FOLLOWLOGS(state, event) {
    state.followLogs = event
  },
}

export const getters = {
  // Whether the user is currently logged in.
  // loggedIn(state) {
  //     return !!state.currentUser
  // },
}

export const actions = {
  // This is automatically run in `src/state/store.js` when the app
  // starts, along with any other actions named `init` in other modules.
  init({ state, dispatch }) {},
}

function startWebsocket(urlPath, onMessage) {
  let webSocket = null
  const ws = location.protocol === 'http:' ? 'ws:' : 'wss:'
  const url = ws + '//' + location.hostname + (location.port ? ':' + location.port : '') + urlPath
  console.log('start reading server state via websocket at ' + url)
  try {
    webSocket = new WebSocket(url)
  } catch (e) {
    store.commit('nusimapp/SET_WEBSOCKET_ERROR', 'could not initially open websocket: ' + e.code)
    setTimeout(() => {
      startWebsocket(urlPath, onMessage)
    }, 1000)
    return
  }
  webSocket.onmessage = onMessage
  webSocket.onerror = function(e) {
    store.commit('nusimapp/SET_WEBSOCKET_ERROR', 'Websocket error ' + e.code)
    switch (e.code) {
      case 'ECONNREFUSED':
        setTimeout(() => {
          startWebsocket(urlPath, onMessage)
        }, 1000)
        break
      default:
        this.lastError = 'error reading state: ' + e
        break
    }
  }
  webSocket.onclose = function(e) {
    console.log('closed with ' + e.code + ' (' + e.reason + ')')
    switch (e.code) {
      case 1000: // CLOSE_NORMAL
        store.commit('nusimapp/SET_WEBSOCKET_CLOSE', 'websocket closed normally')
        break
      default:
        // Abnormal closure
        store.commit('nusimapp/SET_WEBSOCKET_CLOSE', 'websocket closed abnormally')
        setTimeout(() => {
          startWebsocket(urlPath, onMessage)
        }, 1000)
        break
    }
  }
  webSocket.onopen = function() {
    store.commit('nusimapp/SET_WEBSOCKET_OPEN')
  }
}
