const path = require('path');
const pak = require('../package.json');

module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['QuickImageViewComponentDescriptor'],
      },
    },
  },
  dependencies: {
    [pak.name]: {
      root: path.join(__dirname, '..'),
    },
  },
};
