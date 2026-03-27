import AbilityStage from '@ohos.app.ability.AbilityStage';
import hilog from '@ohos.hilog';

export default class MyAbilityStage extends AbilityStage {
  onCreate(): void {
    // MMKV 由 KRMMKVModule 在首次调用时自动初始化，无需手动调用
    hilog.info(0x0000, 'testTag', '%{public}s', 'AbilityStage onCreate');
  }
}
