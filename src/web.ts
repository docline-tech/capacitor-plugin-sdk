import { WebPlugin } from '@capacitor/core';

import type { DoclineSDKPlugin } from './definitions';

export class DoclineSDKWeb extends WebPlugin implements DoclineSDKPlugin {
  constructor() {
    super({
      name: 'DoclineSDK',
      platforms: ['web'],
    });
  }

  async join(options: { code: string, path: string, color: string }): Promise<void> {
    console.log('join', options);
  }
}
