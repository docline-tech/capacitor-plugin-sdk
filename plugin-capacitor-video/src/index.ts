import { registerPlugin } from '@capacitor/core';

import type { DoclineSDKPlugin } from './definitions';

const DoclineSDK = registerPlugin<DoclineSDKPlugin>('DoclineSDK', {
  web: () => import('./web').then(m => new m.DoclineSDKWeb()),
});

export * from './definitions';
export { DoclineSDK };
