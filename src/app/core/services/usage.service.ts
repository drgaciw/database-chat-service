import { Injectable } from '@angular/core';
import { StorageService } from './storage.service';

const RATE_LIMIT_TIMESTAMPS_KEY = 'rateLimitTimestamps';
const DAILY_QUOTA_KEY = 'dailyQuota';

// Defines the rate limit: 5 requests per 60 seconds.
const RATE_LIMIT_COUNT = 5;
const RATE_LIMIT_WINDOW_MS = 60 * 1000;

// Defines the daily quota: 100 requests per day.
const DAILY_QUOTA_LIMIT = 100;

interface DailyQuota {
  count: number;
  date: string; // Stored as YYYY-MM-DD
}

@Injectable({
  providedIn: 'root'
})
export class UsageService {

  constructor(private storageService: StorageService) { }

  /**
   * Checks if the user has made too many requests in the last minute.
   * @returns True if the user is rate-limited, otherwise false.
   */
  isRateLimited(): boolean {
    const timestamps = this.getTimestamps();
    if (timestamps.length < RATE_LIMIT_COUNT) {
      return false;
    }

    const now = Date.now();
    const oldestTimestamp = timestamps[0];

    // If the time elapsed since the oldest request is less than the window, we are rate-limited.
    return (now - oldestTimestamp) < RATE_LIMIT_WINDOW_MS;
  }

  /**
   * Checks if the user has exceeded their daily message quota.
   * @returns True if the quota is exceeded, otherwise false.
   */
  isQuotaExceeded(): boolean {
    const quota = this.getCurrentQuota();
    return quota.count >= DAILY_QUOTA_LIMIT;
  }

  /**
   * Records a new request. This should be called each time a successful request
   * is made to the AI service. It updates both the rate limit timestamps and the daily quota.
   */
  recordNewRequest(): void {
    this.recordTimestamp();
    this.incrementDailyQuota();
  }

  private recordTimestamp(): void {
    const timestamps = this.getTimestamps();
    timestamps.push(Date.now());

    // Keep the array trimmed to the size of the rate limit count.
    while (timestamps.length > RATE_LIMIT_COUNT) {
      timestamps.shift();
    }

    this.storageService.setItem(RATE_LIMIT_TIMESTAMPS_KEY, timestamps);
  }

  private incrementDailyQuota(): void {
    const quota = this.getCurrentQuota();
    quota.count++;
    this.storageService.setItem(DAILY_QUOTA_KEY, quota);
  }

  private getTimestamps(): number[] {
    return this.storageService.getItem<number[]>(RATE_LIMIT_TIMESTAMPS_KEY) || [];
  }

  private getCurrentQuota(): DailyQuota {
    const today = new Date().toISOString().split('T')[0];
    const storedQuota = this.storageService.getItem<DailyQuota>(DAILY_QUOTA_KEY);

    if (storedQuota && storedQuota.date === today) {
      return storedQuota;
    }

    // If no quota is stored or the date is old, return a fresh quota object for today.
    return { count: 0, date: today };
  }
}
