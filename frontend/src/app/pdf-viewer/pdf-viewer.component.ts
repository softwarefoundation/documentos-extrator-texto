import {Component, ChangeDetectionStrategy, ViewEncapsulation} from '@angular/core';
import {NgxExtendedPdfViewerModule, NgxExtendedPdfViewerService, pdfDefaultOptions} from 'ngx-extended-pdf-viewer';
import {DialogModule} from "primeng/dialog";
import {ButtonModule} from "primeng/button";

@Component({
  selector: 'app-pdf-viewer',
  templateUrl: './pdf-viewer.component.html',
  styleUrls: ['./pdf-viewer.component.css'],
  standalone: true,
  imports: [NgxExtendedPdfViewerModule, DialogModule, ButtonModule],
  providers: [NgxExtendedPdfViewerService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.Emulated
})
export class PdfViewerComponent {
  visible: boolean = true;
  showDialog: boolean = true;
  /** In most cases, you don't need the NgxExtendedPdfViewerService. It allows you
   *  to use the "find" api, to extract text and images from a PDF file,
   *  to print programmatically, and to show or hide layers by a method call.
   */

  pdfbase64: string = "";

  constructor(private pdfService: NgxExtendedPdfViewerService) {
    /* More likely than not you don't need to tweak the pdfDefaultOptions.
       They are a collecton of less frequently used options.
       To illustrate how they're used, here are two example settings: */
    // pdfDefaultOptions.doubleTapZoomFactor = '150%'; // The default value is '200%'
    // pdfDefaultOptions.maxCanvasPixels = 4096 * 4096 * 5; // The default value is 4096 * 4096 pixels,
    // but most devices support much higher resolutions.
    // Increasing this setting allows your users to use higher zoom factors,
    // trading image quality for performance.


  }
}
