import { Injectable, inject } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class PaginatorIntlService extends MatPaginatorIntl {
  private readonly translate = inject(TranslateService);

  constructor() {
    super();

    this.updateLabels();

    this.translate.onLangChange.subscribe(() => {
      this.updateLabels();
    });
  }

  private updateLabels(): void {
    this.itemsPerPageLabel = this.translate.instant('PAGINATOR.ITEMS_PER_PAGE');
    this.nextPageLabel = this.translate.instant('PAGINATOR.NEXT_PAGE');
    this.previousPageLabel = this.translate.instant('PAGINATOR.PREVIOUS_PAGE');
    this.firstPageLabel = this.translate.instant('PAGINATOR.FIRST_PAGE');
    this.lastPageLabel = this.translate.instant('PAGINATOR.LAST_PAGE');

    this.changes.next();
  }
}
