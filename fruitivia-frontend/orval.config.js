module.exports = {
  fruitivia: {
    input: {
      target: 'http://localhost:8080/v3/api-docs',
    },
    output: {
      mode: 'tags-split',
      target: 'src/api/generated/fruitivia.ts',
      schemas: 'src/api/generated/model',
      client: 'react-query',
      mock: false,
      override: {
        mutator: {
          path: 'src/services/custom-instance.ts',
          name: 'customInstance',
        },
      },
    },
  },
};
