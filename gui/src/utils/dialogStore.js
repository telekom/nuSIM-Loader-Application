export default {
  store(componentName, componentData) {
    const userInput = JSON.parse(window.localStorage.getItem('userInput') || '{}')
    userInput[componentName] = componentData

    window.localStorage.setItem('userInput', JSON.stringify(userInput))
  },

  load(componentName) {
    const userInput = JSON.parse(window.localStorage.getItem('userInput') || '{}')
    return userInput[componentName] || {}
  },
}
