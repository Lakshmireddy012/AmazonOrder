package com.example.demo.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@SequenceGenerator(initialValue = 25060, name = "idgen", sequenceName = "entityaseq1")
@Table(name = "OrderLine")
public class OrderLine
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
  private Long id;
  @Column(name = "OrderNumber")
  private String orderNumber;
  @Column(name = "AcceptationState")
  private String acceptationState;
  @Column(name = "CategoryCode")
  private String categoryCode;
  @Column(name = "DeliveryDateMax")
  private String deliveryDateMax;
  @Column(name = "DeliveryDateMin")
  private String deliveryDateMin;
  @Column(name = "HasClaim")
  private String hasClaim;
  @Column(name = "InitialPrice")
  private String initialPrice;
  @Column(name = "IsCDAV")
  private String isCDAV;
  @Column(name = "IsNegotiated")
  private String isNegotiated;
  @Column(name = "IsProductEanGenerated")
  private String isProductEanGenerated;
  @Column(name = "Name")
  private String name;
  @Column(name = "ProductCondition")
  private String productCondition;
  @Column(name = "ProductEan")
  private String productEan;
  @Column(name = "ProductId")
  private String productId;
  @Column(name = "PurchasePrice")
  private String purchasePrice;
  @Column(name = "Quantity")
  private String quantity;
  @Column(name = "RefundShippingCharges")
  private String refundShippingCharges;
  @Column(name = "RowId")
  private String rowId;
  @Column(name = "SellerProductId")
  private String sellerProductId;
  @Column(name = "ShippingDateMax")
  private String sippingDateMax;
  @Column(name = "ShippingDateMin")
  private String sippingDateMin;
  @Column(name = "Sku")
  private String sku;
  @Column(name = "SkuParent")
  private String skuParent;
  @Column(name = "UnitAdditionalShippingCharges")
  private String unitAdditionalShippingCharges;
  @Column(name = "UnitShippingCharges")
  private String unitShippingCharges;
  @Column(name = "Asin")
  private String asin;
  @Column(name = "Asin2", columnDefinition = "")
  private String asin2;
  
  public String getAsin2() { return this.asin2; }


  
  public void setAsin2(String asin2) { this.asin2 = asin2; }


  
  public String getAsin() { return this.asin; }


  
  public void setAsin(String asin) { this.asin = asin; }


  
  public String getOrderNumber() { return this.orderNumber; }


  
  public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }


  
  public String getAcceptationState() { return this.acceptationState; }


  
  public void setAcceptationState(String acceptationState) { this.acceptationState = acceptationState; }


  
  public String getCategoryCode() { return this.categoryCode; }


  
  public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }


  
  public String getDeliveryDateMax() { return this.deliveryDateMax; }


  
  public void setDeliveryDateMax(String deliveryDateMax) { this.deliveryDateMax = deliveryDateMax; }


  
  public String getDeliveryDateMin() { return this.deliveryDateMin; }


  
  public void setDeliveryDateMin(String deliveryDateMin) { this.deliveryDateMin = deliveryDateMin; }


  
  public String getHasClaim() { return this.hasClaim; }


  
  public void setHasClaim(String hasClaim) { this.hasClaim = hasClaim; }


  
  public String getInitialPrice() { return this.initialPrice; }


  
  public void setInitialPrice(String initialPrice) { this.initialPrice = initialPrice; }


  
  public String getIsCDAV() { return this.isCDAV; }


  
  public void setIsCDAV(String isCDAV) { this.isCDAV = isCDAV; }


  
  public String getIsNegotiated() { return this.isNegotiated; }


  
  public void setIsNegotiated(String isNegotiated) { this.isNegotiated = isNegotiated; }


  
  public String getIsProductEanGenerated() { return this.isProductEanGenerated; }


  
  public void setIsProductEanGenerated(String isProductEanGenerated) { this.isProductEanGenerated = isProductEanGenerated; }


  
  public String getName() { return this.name; }


  
  public void setName(String name) { this.name = name; }


  
  public String getProductCondition() { return this.productCondition; }


  
  public void setProductCondition(String productCondition) { this.productCondition = productCondition; }


  
  public String getProductEan() { return this.productEan; }


  
  public void setProductEan(String productEan) { this.productEan = productEan; }


  
  public String getProductId() { return this.productId; }


  
  public void setProductId(String productId) { this.productId = productId; }


  
  public String getPurchasePrice() { return this.purchasePrice; }


  
  public void setPurchasePrice(String purchasePrice) { this.purchasePrice = purchasePrice; }


  
  public String getQuantity() { return this.quantity; }


  
  public void setQuantity(String quantity) { this.quantity = quantity; }


  
  public String getRefundShippingCharges() { return this.refundShippingCharges; }


  
  public void setRefundShippingCharges(String refundShippingCharges) { this.refundShippingCharges = refundShippingCharges; }


  
  public String getRowId() { return this.rowId; }


  
  public void setRowId(String rowId) { this.rowId = rowId; }


  
  public String getSellerProductId() { return this.sellerProductId; }


  
  public void setSellerProductId(String sellerProductId) { this.sellerProductId = sellerProductId; }


  
  public String getSippingDateMax() { return this.sippingDateMax; }


  
  public void setSippingDateMax(String sippingDateMax) { this.sippingDateMax = sippingDateMax; }


  
  public String getSippingDateMin() { return this.sippingDateMin; }


  
  public void setSippingDateMin(String sippingDateMin) { this.sippingDateMin = sippingDateMin; }


  
  public String getSku() { return this.sku; }


  
  public void setSku(String sku) { this.sku = sku; }


  
  public String getSkuParent() { return this.skuParent; }


  
  public void setSkuParent(String skuParent) { this.skuParent = skuParent; }


  
  public String getUnitAdditionalShippingCharges() { return this.unitAdditionalShippingCharges; }


  
  public void setUnitAdditionalShippingCharges(String unitAdditionalShippingCharges) { this.unitAdditionalShippingCharges = unitAdditionalShippingCharges; }


  
  public String getUnitShippingCharges() { return this.unitShippingCharges; }


  
  public void setUnitShippingCharges(String unitShippingCharges) { this.unitShippingCharges = unitShippingCharges; }

  
  public OrderLine(String orderNumber, String acceptationState, String categoryCode, String deliveryDateMax, String deliveryDateMin, String hasClaim, String initialPrice, String isCDAV, String isNegotiated, String isProductEanGenerated, String name, String productCondition, String productEan, String productId, String purchasePrice, String quantity, String refundShippingCharges, String rowId, String sellerProductId, String sippingDateMax, String sippingDateMin, String sku, String skuParent, String unitAdditionalShippingCharges, String unitShippingCharges, String asin, String asin2) {
    this.orderNumber = orderNumber;
    this.acceptationState = acceptationState;
    this.categoryCode = categoryCode;
    this.deliveryDateMax = deliveryDateMax;
    this.deliveryDateMin = deliveryDateMin;
    this.hasClaim = hasClaim;
    this.initialPrice = initialPrice;
    this.isCDAV = isCDAV;
    this.isNegotiated = isNegotiated;
    this.isProductEanGenerated = isProductEanGenerated;
    this.name = name;
    this.productCondition = productCondition;
    this.productEan = productEan;
    this.productId = productId;
    this.purchasePrice = purchasePrice;
    this.quantity = quantity;
    this.refundShippingCharges = refundShippingCharges;
    this.rowId = rowId;
    this.sellerProductId = sellerProductId;
    this.sippingDateMax = sippingDateMax;
    this.sippingDateMin = sippingDateMin;
    this.sku = sku;
    this.skuParent = skuParent;
    this.unitAdditionalShippingCharges = unitAdditionalShippingCharges;
    this.unitShippingCharges = unitShippingCharges;
    this.asin = asin;
    this.asin2 = asin2;
  }
  
  public OrderLine() {}
}

