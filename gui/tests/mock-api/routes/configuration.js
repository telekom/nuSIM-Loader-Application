var configuration = require('../resources/configuration.json')

module.exports = app => {
  app.get('/api/v1/configuration', (request, response) => {
    response.json(configuration)
  })
  app.put('/api/v1/configuration', (request, response) => {
    configuration = request.body
    response.json({})
  })
}
