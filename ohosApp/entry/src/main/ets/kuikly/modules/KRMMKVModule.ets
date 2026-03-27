import { KRAny, KuiklyRenderBaseModule, KuiklyRenderCallback } from '@kuikly-open/render';
import { MMKV } from '@tencent/mmkv';
import { common } from '@kit.AbilityKit';

/**
 * HarmonyOS 原生 MMKV Module
 *
 * 接收 KMP 层 MMKVModule 的调用，委派到 MMKV HarmonyOS SDK 执行。
 * MMKV 会在首次调用时自动初始化，业务方无需手动调用 MMKV.initialize()。
 */
export class KRMMKVModule extends KuiklyRenderBaseModule {
  static readonly MODULE_NAME = "KuiklyMMKVModule";
  private static mmkvInitialized: boolean = false;

  syncMode(): boolean {
    return true; // 支持同步调用
  }

  /**
   * 确保 MMKV 已初始化（懒初始化，首次调用时自动执行）
   */
  private ensureMMKVInitialized(): void {
    if (KRMMKVModule.mmkvInitialized) {
      return;
    }
    try {
      const context = getContext(this) as common.UIAbilityContext;
      MMKV.initialize(context.getApplicationContext());
      KRMMKVModule.mmkvInitialized = true;
    } catch (e) {
      // 可能已经被初始化过（例如业务方也调用了），忽略错误
      KRMMKVModule.mmkvInitialized = true;
    }
  }

  call(method: string, params: KRAny, callback: KuiklyRenderCallback | null): KRAny {
    this.ensureMMKVInitialized();
    switch (method) {
      case 'encode':
        return this.encode(params);
      case 'decode':
        return this.decode(params);
      case 'containsKey':
        return this.containsKey(params);
      case 'removeValueForKey':
        return this.removeValueForKey(params);
      case 'removeValuesForKeys':
        return this.removeValuesForKeys(params);
      case 'allKeys':
        return this.allKeys(params, callback);
      case 'count':
        return this.doCount(params);
      case 'totalSize':
        return this.doTotalSize(params);
      case 'actualSize':
        return this.doActualSize(params);
      case 'clearAll':
        return this.doClearAll(params);
      case 'trim':
        return this.doTrim(params);
      case 'batchEncode':
        return this.batchEncode(params);
      default:
        return null;
    }
  }

  private getMMKV(mmapID: string | null): MMKV {
    if (mmapID && mmapID.length > 0) {
      return MMKV.mmkvWithID(mmapID);
    }
    return MMKV.defaultMMKV();
  }

  private encode(params: KRAny): KRAny {
    if (!params) return null;
    const json: Record<string, Object> = JSON.parse(params as string);
    const key = json['key'] as string;
    const type = json['type'] as string;
    const mmapID = json['mmapID'] as string | null;
    const mmkv = this.getMMKV(mmapID);

    switch (type) {
      case 'string':
        mmkv.encodeString(key, json['value'] as string);
        break;
      case 'int':
        mmkv.encodeInt32(key, json['value'] as number);
        break;
      case 'long':
        mmkv.encodeInt64(key, BigInt(json['value'] as number));
        break;
      case 'float':
      case 'double':
        mmkv.encodeDouble(key, json['value'] as number);
        break;
      case 'bool':
        mmkv.encodeBool(key, json['value'] as boolean);
        break;
      default:
        break;
    }
    return null;
  }

  private decode(params: KRAny): string {
    if (!params) return '';
    const json: Record<string, Object> = JSON.parse(params as string);
    const key = json['key'] as string;
    const type = json['type'] as string;
    const mmapID = json['mmapID'] as string | null;
    const mmkv = this.getMMKV(mmapID);

    switch (type) {
      case 'string': {
        const defaultValue = (json['defaultValue'] as string) || '';
        return mmkv.decodeString(key) || defaultValue;
      }
      case 'int': {
        const defaultValue = (json['defaultValue'] as number) || 0;
        return (mmkv.decodeInt32(key, defaultValue) ?? defaultValue).toString();
      }
      case 'long': {
        const defaultValue = BigInt((json['defaultValue'] as number) || 0);
        return (mmkv.decodeInt64(key, defaultValue) ?? defaultValue).toString();
      }
      case 'float':
      case 'double': {
        const defaultValue = (json['defaultValue'] as number) || 0;
        return (mmkv.decodeDouble(key, defaultValue) ?? defaultValue).toString();
      }
      case 'bool': {
        const defaultValue = (json['defaultValue'] as boolean) || false;
        return mmkv.decodeBool(key, defaultValue) ? 'true' : 'false';
      }
      default:
        return '';
    }
  }

  private containsKey(params: KRAny): string {
    if (!params) return 'false';
    const json: Record<string, Object> = JSON.parse(params as string);
    const key = json['key'] as string;
    const mmapID = json['mmapID'] as string | null;
    return this.getMMKV(mmapID).containsKey(key) ? 'true' : 'false';
  }

  private removeValueForKey(params: KRAny): KRAny {
    if (!params) return null;
    const json: Record<string, Object> = JSON.parse(params as string);
    const key = json['key'] as string;
    const mmapID = json['mmapID'] as string | null;
    this.getMMKV(mmapID).removeValueForKey(key);
    return null;
  }

  private removeValuesForKeys(params: KRAny): KRAny {
    if (!params) return null;
    const json: Record<string, Object> = JSON.parse(params as string);
    const keys = json['keys'] as string[];
    const mmapID = json['mmapID'] as string | null;
    if (keys) {
      this.getMMKV(mmapID).removeValuesForKeys(keys);
    }
    return null;
  }

  private allKeys(params: KRAny, callback: KuiklyRenderCallback | null): KRAny {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    const keys: string[] = this.getMMKV(mmapID).allKeys() as string[] || [];
    const keysStr: string = keys.join(',');
    callback?.({ 'keys': keysStr });
    return null;
  }

  private doCount(params: KRAny): string {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    return this.getMMKV(mmapID).count().toString();
  }

  private doTotalSize(params: KRAny): string {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    return this.getMMKV(mmapID).totalSize().toString();
  }

  private doActualSize(params: KRAny): string {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    return this.getMMKV(mmapID).actualSize().toString();
  }

  private doClearAll(params: KRAny): KRAny {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    this.getMMKV(mmapID).clearAll();
    return null;
  }

  private doTrim(params: KRAny): KRAny {
    const json: Record<string, Object> = params ? JSON.parse(params as string) : {};
    const mmapID = json['mmapID'] as string | null;
    this.getMMKV(mmapID).trim();
    return null;
  }

  private batchEncode(params: KRAny): KRAny {
    if (!params) return null;
    const json: Record<string, Object> = JSON.parse(params as string);
    const entries = json['entries'] as Record<string, Object>[];
    const mmapID = json['mmapID'] as string | null;
    const mmkv = this.getMMKV(mmapID);

    for (const entry of entries) {
      const key = entry['key'] as string;
      const type = (entry['type'] as string) || 'string';
      switch (type) {
        case 'string':
          mmkv.encodeString(key, entry['value'] as string);
          break;
        case 'int':
          mmkv.encodeInt32(key, entry['value'] as number);
          break;
        case 'long':
          mmkv.encodeInt64(key, BigInt(entry['value'] as number));
          break;
        case 'float':
        case 'double':
          mmkv.encodeDouble(key, entry['value'] as number);
          break;
        case 'bool':
          mmkv.encodeBool(key, entry['value'] as boolean);
          break;
        default:
          break;
      }
    }
    return null;
  }
}
