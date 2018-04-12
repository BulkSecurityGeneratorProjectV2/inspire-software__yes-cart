/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Config } from '../config/env.config';
import { ShopVO, PriceListVO, TaxVO, TaxConfigVO, PromotionVO, PromotionCouponVO, CartVO, PromotionTestVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class PricingService {

  private _serviceBaseUrl = Config.API + 'service/pricing';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('PricingService constructed');
  }

  /**
   * Get list of all price lists, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredPriceLists(shop:ShopVO, currency:string, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/price/shop/' + shop.shopId + '/currency/' + currency + '/filtered/' + max, body, options)
        .map(res => <PriceListVO[]> this.json(res))
        .catch(this.handleError);
  }

  /**
   * Get price list, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPriceListById(priceId:number) {
    return this.http.get(this._serviceBaseUrl + '/price/' + priceId)
      .map(res => <PriceListVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update price list.
   * @param price price list
   * @returns {Observable<R>}
   */
  savePriceList(price:PriceListVO) {

    let body = JSON.stringify(price);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (price.skuPriceId > 0) {
      return this.http.post(this._serviceBaseUrl + '/price', body, options)
        .map(res => <PriceListVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/price', body, options)
        .map(res => <PriceListVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove price list.
   * @param price list price list
   * @returns {Observable<R>}
   */
  removePriceList(price:PriceListVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/price/' + price.skuPriceId, options)
      .catch(this.handleError);
  }



  /**
   * Get list of all taxes, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredTax(shop:ShopVO, currency:string, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/tax/shop/' + shop.code + '/currency/' + currency + '/filtered/' + max, body, options)
      .map(res => <TaxVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get tax, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getTaxById(taxId:number) {
    return this.http.get(this._serviceBaseUrl + '/tax/' + taxId)
      .map(res => <TaxVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update tax.
   * @param tax tax
   * @returns {Observable<R>}
   */
  saveTax(tax:TaxVO) {

    let body = JSON.stringify(tax);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (tax.taxId > 0) {
      return this.http.post(this._serviceBaseUrl + '/tax', body, options)
        .map(res => <TaxVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/tax', body, options)
        .map(res => <TaxVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove tax.
   * @param tax tax
   * @returns {Observable<R>}
   */
  removeTax(tax:TaxVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/tax/' + tax.taxId, options)
      .catch(this.handleError);
  }



  /**
   * Get list of all tax configs, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredTaxConfig(tax:TaxVO, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/taxconfig/tax/' + tax.taxId + '/filtered/' + max, body, options)
      .map(res => <TaxConfigVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get tax config, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getTaxConfigById(taxConfigId:number) {
    return this.http.get(this._serviceBaseUrl + '/taxconfig/' + taxConfigId)
      .map(res => <TaxConfigVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create tax config.
   * @param taxConfig config
   * @returns {Observable<R>}
   */
  createTaxConfig(taxConfig:TaxConfigVO) {

    let body = JSON.stringify(taxConfig);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl + '/taxconfig', body, options)
      .map(res => <TaxConfigVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Remove tax config.
   * @param taxConfig tax config
   * @returns {Observable<R>}
   */
  removeTaxConfig(taxConfig:TaxConfigVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/taxconfig/' + taxConfig.taxConfigId, options)
      .catch(this.handleError);
  }



  /**
   * Test rule for given shop in specified currency,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  testPromotions(shop:ShopVO, currency:string, test:PromotionTestVO) {

    let body = JSON.stringify(test);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/promotion/test/shop/' + shop.code + '/currency/' + currency + '/', body, options)
      .map(res => <CartVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all promotions, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredPromotions(shop:ShopVO, currency:string, filter:string, types:string[], actions:string[], max:number) {

    let body = JSON.stringify({ filter: filter, types: types, actions: actions });
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/promotion/shop/' + shop.code + '/currency/' + currency + '/filtered/' + max, body, options)
      .map(res => <PromotionVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get promotion, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPromotionById(promotionId:number) {
    return this.http.get(this._serviceBaseUrl + '/promotion/' + promotionId)
      .map(res => <PromotionVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update promotion.
   * @param promotion promotion
   * @returns {Observable<R>}
   */
  savePromotion(promotion:PromotionVO) {

    let body = JSON.stringify(promotion);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (promotion.promotionId > 0) {
      return this.http.post(this._serviceBaseUrl + '/promotion', body, options)
        .map(res => <PromotionVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/promotion', body, options)
        .map(res => <PromotionVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove promotion.
   * @param promotion promotion
   * @returns {Observable<R>}
   */
  removePromotion(promotion:PromotionVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/promotion/' + promotion.promotionId, options)
      .catch(this.handleError);
  }


  /**
   * Update promotion state.
   * @param promotion promotion
   * @param state enabled or disabled
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updatePromotionDisabledFlag(promotion:PromotionVO, state:boolean) {
    LogUtil.debug('PricingService change state promotion ' + promotion.promotionId + ' to ' + state ? 'online' : 'offline');

    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/promotion/offline/' + promotion.promotionId + '/' + state, null, options)
      .catch(this.handleError);
  }


  /**
   * Get list of all promotion coupons, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredPromotionCoupons(promotion:PromotionVO, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/promotioncoupon/' + promotion.promotionId + '/filtered/' + max, body, options)
      .map(res => <PromotionCouponVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create promotion coupons.
   * @param coupons coupons
   * @returns {Observable<R>}
   */
  createPromotionCoupons(coupons:PromotionCouponVO) {

    let body = JSON.stringify(coupons);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl + '/promotioncoupon', body, options)
      .map(res => <PromotionCouponVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Remove promotion coupon.
   * @param coupon coupon
   * @returns {Observable<R>}
   */
  removePromotionCoupon(coupon:PromotionCouponVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/promotioncoupon/' + coupon.promotioncouponId, options)
      .catch(this.handleError);
  }


  private json(res: Response): any {
    let contentType = res.headers.get('Content-Type');
    LogUtil.debug('Processing JSON response', contentType, res.text().includes('loginForm'));
    if (contentType != null && contentType.includes('text/html') && res.text().includes('loginForm')) {
      throw new Error('MODAL_ERROR_MESSAGE_AUTH');
    }
    return res.json();
  }


  private handleError (error:any) {

    LogUtil.error('PricingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
