import { Pipe, PipeTransform, ElementRef, NgZone, ViewContainerRef } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { MarkdownService } from 'ngx-markdown';
import { first } from 'rxjs/operators';

@Pipe({
  name: 'markdown'
})
export class MarkdownPipe implements PipeTransform {
  constructor(
    private domSanitizer: DomSanitizer,
    private elementRef: ElementRef<HTMLElement>,
    private markdownService: MarkdownService,
    private viewContainerRef: ViewContainerRef,
    private zone: NgZone
  ) {}

  transform(value: string, options?: any): SafeHtml {
    if (value == null) {
      return '';
    }
    if (typeof value !== 'string') {
      console.error(`MarkdownPipe has been invoked with an invalid value type [${typeof value}]`);
      return value;
    }
    
    const markdown = this.markdownService.parse(value, options);
    this.zone.onStable
      .pipe(first())
      .subscribe(() => this.markdownService.render(this.elementRef.nativeElement, options, this.viewContainerRef));
    
    return this.domSanitizer.bypassSecurityTrustHtml(markdown);
  }
}
