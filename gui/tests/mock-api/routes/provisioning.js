module.exports = app => {
  app.get('/api/v1/provisioning/loadCertificates', (request, response) => {
    response.json({ total: 50, processed: 5, success: 3, error: 2})
  })
  app.post('/api/v1/provisioning/requestProfiles', (request, response) => {
    response.json({ referenceId: '1542977325', retrieveTime: '2018-11-23T15:48:45.381+01:00[Europe/Berlin]' })
  })
  app.get('/api/v1/provisioning/referenceIds', (request, response) => {
    response.json({
      entries: [
        {
          referenceId: '726387213',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '712638723',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '286423844',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '542977325',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '123213123',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '354534543',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '154297325',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '128213123',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
        {
          referenceId: '334534543',
          refInfo1: 'refInfo1 Text',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          requestedAt: '2018-11-23T13:48:45.388',
          requestCount: 7,
          availableAt: '2018-11-23T15:48:45.381',
        },
      ],
    })
  })
  app.post('/api/v1/provisioning/retrieveProfiles', (request, response) => {
    response.json({ referenceId: '1542977325', retrievedTotal: 5, retrievedGood: 5, addedLocally: 5 })
  })
  app.post('/api/v1/provisioning/queryProfileStock', (request, response) => {
    response.json({
      stockList: [
        {
          refInfo1: 'Product=A1',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '100',
        },
        {
          refInfo1: 'Product=B2',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '200',
        },
        {
          refInfo1: 'Product=C3',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '300',
        },
        {
          refInfo1: 'Product=D1',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '1200',
        },
        {
          refInfo1: 'Product=E2',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '2340',
        },
        {
          refInfo1: 'Product=F3',
          refInfo2: 'refInfo2 Text',
          refInfo3: 'refInfo3 Text',
          available: '320',
        },
      ],
    })
  })
  app.put('/api/v1/provisioning/status', (request, response) => {
    response.json(true)
  })
  app.get('/api/v1/logging/live', (request, response) => {
    response.json([
      {
        loglevel: 'DEBUG',
        message:
          'IN #70: GET Request for /api/v1/provisioning/loadCertificates [ timestamp: 2018-11-23T12:52:39.823Z, remoteAddress: 127.0.0.1:57106, localAddress: 127.0.0.1:8088, uri: /api/v1/provisioning/loadCertificates, headers: [x-forwarded-host:[localhost:8080], x-forwarded-proto:[http], x-forwarded-port:[8080], x-forwarded-for:[127.0.0.1], cookie:[NUSIMAPP-PATH-PREFIX=/foo; NUSIMAPP-PATH-PREFIX=/foo; Idea-e1b9fb2c=1d306112-9692-491d-865c-86d950563308; Language_In_Use=; SID=OQuWjnQHHuBZKJgvVHqNTtcWmGsylvMBNgxztPICjjdTvjQqsIFmbHinndoayCBNIAmEpUgaGkv; cb6b71405c3439199ce535af1a2e2f79=b4e01a2a63c7567c62f9ce39c57b592e; Idea-499b280b=24e6982b-3431-4ccc-bc54-4a0b9564b8bd; _ga=GA1.1.1121129335.1537193674; cb-enabled=enabled], accept-language:[de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7], accept-encoding:[gzip, deflate, br], referer:[http://localhost:8080/], user-agent:[Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36], accept:[application/json, text/plain, */*], cache-control:[no-cache], pragma:[no-cache], connection:[close], host:[127.0.0.1:8088]], body:  ]',
        t: 1542977559825,
      },
      {
        loglevel: 'INFO',
        message: 'start retrieving certificates for EIDs from CM',
        t: 1542977559825,
      },
      { loglevel: 'DEBUG', message: 'commit                   ', t: 1542977559833 },
      {
        loglevel: 'INFO',
        message: 'finished retrieving certificates. 0 EIDs handled',
        t: 1542977559834,
      },
      {
        loglevel: 'DEBUG',
        message:
          'IN #70: Response for /api/v1/provisioning/loadCertificates [ status: "200:OK", headers: [content-type:[text/plain;charset=UTF-8], connection:[close], content-encoding:[gzip], transfer-encoding:[chunked]], body: null ]',
        t: 1542977559836,
      },
    ])
  })
}
